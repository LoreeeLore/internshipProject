package com.studlabs.bll.services;

import com.studlabs.bll.exceptions.*;
import com.studlabs.bll.model.Image;
import com.studlabs.bll.model.Message;
import com.studlabs.dao.MessageDao;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class MessageServiceImpl implements MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private RestHighLevelClient client;

    @Value("${elasticsearch.index}")
    private String indexFull;


    @Override
    public Message save(Message message)
            throws BllException, BadRequestException {
        try {
            validateImages(message.getImages(), message);
            validateSaveImages(message.getImages());

            Message savedMessage = messageDao.save(message);

            indexOneMessage(savedMessage);

            return savedMessage;
        } catch (InvalidParamException e) {
            logger.warn(e.getMessage());
            throw new BadRequestException(e.getMessage(), e);
        } catch (DaoException e) {
            logger.warn(e.getMessage());
            throw new BllException(e.getMessage(), e);
        } catch (IOException e) {
            throw new BllException(e.getMessage(), e);
        }
    }

    @Override
    @SuppressWarnings("Duplicates")
    public void update(Message message)
            throws BllException, NotFoundException, BadRequestException {
        validateImages(message.getImages(), message);
        try {
            Map<String, Object> updatedValues = new HashMap<>();
            updatedValues.put("text", message.getText());
            UpdateRequest request = new UpdateRequest(indexFull, message.getId() + "").doc(updatedValues);
            client.update(request, RequestOptions.DEFAULT);
            messageDao.update(message);
        } catch (InvalidParamException e) {
            logger.warn(e.getMessage());
            throw new BadRequestException(e.getMessage(), e);
        } catch (NoMessageException e) {
            logger.warn(e.getMessage());
            throw new NotFoundException(e.getMessage(), e);
        } catch (DaoException | IOException e) {
            logger.warn(e.getMessage());
            throw new BllException(e.getMessage(), e);
        }
    }

    @Override
    public void remove(int id) throws BllException, NotFoundException {
        try {
            deleteElasticsearchEntry(id);
            messageDao.remove(id);
        } catch (NoMessageException e) {
            logger.warn(e.getMessage());
            throw new NotFoundException(e.getMessage(), e);
        } catch (DaoException | IOException e) {
            logger.warn(e.getMessage());
            throw new BllException(e.getMessage(), e);
        }
    }

    @Override
    public Optional<Message> findById(int id) throws BllException {
        try {
            return messageDao.findById(id);
        } catch (DaoException e) {
            logger.warn(e.getMessage());
            throw new BllException(e.getMessage(), e);
        }
    }

    @Override
    public void checkIfMessageExists(int threadId, int msgId)
            throws BllException, NoMessageException {
        Optional<Message> messageFromThread = null;
        try {
            messageFromThread = messageDao.getMessageFromThread(threadId, msgId);
        } catch (DaoException e) {
            logger.warn(e.getMessage());
            throw new BllException(e.getMessage(), e);
        }

        messageFromThread.orElseThrow(()
                -> new NoMessageException("Message from thread cannot be found"));
    }

    @Override
    public List<Message> findAllSorted(int threadId, Optional<String> sortBy,
                                       Optional<String> order, Optional<Integer> limit, Optional<Integer> offset)
            throws BllException, BadRequestException {
        try {
            return messageDao.findAll(threadId, sortBy, order, limit, offset);
        } catch (InvalidParamException e) {
            logger.warn(e.getMessage());
            throw new BadRequestException(e.getMessage(), e);
        } catch (DaoException e) {
            logger.warn(e.getMessage());
            throw new BllException(e.getMessage(), e);
        }
    }

    @Override
    public List<Message> findAll() throws BllException {
        try {
            return messageDao.findAll();
        } catch (DaoException e) {
            logger.warn(e.getMessage());
            throw new BllException(e.getMessage(), e);
        }
    }

    private void validateSaveImages(List<Image> images) throws BllException {
        for (Image image : images) {
            if (StringUtils.isBlank(image.getImage())) {
                throw new BllException("Image cannot be empty");
            }
        }
    }

    private void validateImages(List<Image> images,
                                Message message) throws BadRequestException {
        for (Image image : images) {
            if (image.getMessageId() == null) {
                image.setMessageId(message.getId());
            } else if (!image.getMessageId().equals(message.getId())) {
                throw new BadRequestException("bad message id in image");
            }
        }
    }

    public void deleteElasticsearchEntry(int id) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(indexFull, id + "");
        client.delete(deleteRequest, RequestOptions.DEFAULT);
    }

    @Override
    public void indexAllMessages() throws BllException {
        try {
            List<Message> allMessages = messageDao.findAll();
            for (Message m : allMessages) {
                indexOneMessage(m);
            }
        } catch (DaoException | IOException e) {
            logger.warn(e.getMessage());
            throw new BllException(e.getMessage(), e);
        }
    }


    private void indexOneMessage(Message m) throws java.io.IOException {
        String jsonString = "{" +
                "\"text\":\"" + m.getText() + "\"" +
                "}";
        IndexRequest request = new IndexRequest(indexFull);
        request.source(jsonString, XContentType.JSON);
        request.id(m.getId() + "");
        client.index(request, RequestOptions.DEFAULT);
    }


    //elastic search read
    public List<String> fullTextSearch(String key) throws IOException {

        List<String> result = new ArrayList<>();
        logger.info("Searching for messages with the group of letters: {} ", key);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(10).
                query(QueryBuilders.matchQuery("text", key));

        SearchRequest searchRequest = new SearchRequest(new String[]{indexFull}, searchSourceBuilder);
        SearchResponse searchResponse;
        searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            result.add(hit.getId());
        }
        return result;
    }
}

