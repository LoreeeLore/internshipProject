package com.studlabs.dao;

import com.studlabs.bll.exceptions.DaoException;
import com.studlabs.bll.exceptions.InvalidParamException;
import com.studlabs.bll.exceptions.NoMessageException;
import com.studlabs.bll.model.*;
import com.studlabs.dao.mappers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class MessageDaoImpl implements MessageDao {

    private static final Logger logger = LoggerFactory.getLogger(MessageDaoImpl.class);
    private final DownvoteExtractor downvoteExtractor = new DownvoteExtractor();
    private final ForumMessageMapper forumMessageMapper = new ForumMessageMapper();
    private final UserTagExtractor userTagExtractor = new UserTagExtractor();
    private final ImagesExtractor imagesExtractor = new ImagesExtractor();
    private final ForumMessageExtractor forumMessageExtractor = new ForumMessageExtractor();
    private final UpvoteExtractor upvoteExtractor = new UpvoteExtractor();
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ImageDaoImpl imageDao;
    @Autowired
    private UserDaoImpl userDao;

    @Autowired
    private MessageTagDaoImpl messageTagDao;

    @Override
    public List<Message> findAll(int threadId, Optional<String> sortBy, Optional<String> order,
                                 Optional<Integer> limit, Optional<Integer> offset)
            throws DaoException, InvalidParamException {
        return findAllMessages(Optional.of(threadId), sortBy, order, limit, offset);
    }

    @Override
    public List<Message> findAll() throws DaoException {
        try {
            return findAllMessages(Optional.empty(), Optional.empty(), Optional.empty(),
                    Optional.empty(), Optional.empty());
        } catch (InvalidParamException e) {
            //this exception cannot be thrown by this method
            logger.info("InvalidParamException was thrown in findAllOpen() -> runtime error:");
            throw new RuntimeException("Internal Server error!");
        }
    }

    @Override
    @TransactionalWithRollback
    public Message save(Message forumMessage) throws DaoException, InvalidParamException {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
        insert.setTableName("message");
        insert.usingGeneratedKeyColumns("id");

        final Map<String, Object> map = new HashMap<>();
        map.put("thread_id", forumMessage.getThreadId());
        map.put("user", forumMessage.getUser());
        map.put("text", forumMessage.getText());

        if (forumMessage.getDate() != null) {
            map.put("date", forumMessage.getDate());
        } else {
            logger.info("save: date not present in message, setting it to current date");
            forumMessage.setDate(LocalDateTime.now());
            map.put("date", forumMessage.getDate());
        }

        try {
            forumMessage.setId(insert.executeAndReturnKey(map).intValue());
        } catch (DataIntegrityViolationException e) {
            logger.warn(e.getMessage());
            throw new InvalidParamException("cannot save message - bad parameters");
        } catch (DataAccessException e) {
            logger.warn(e.getMessage());
            throw new DaoException("cannot save message - database error");
        }

        //save images
        for (Image image : forumMessage.getImages()) {
            image.setMessageId(forumMessage.getId());
            imageDao.save(image);
        }

        //save user tags
        insert = new SimpleJdbcInsert(jdbcTemplate);
        insert.setTableName("message_tag_user");
        map.clear();
        map.put("message_id", forumMessage.getId());

        for (String userTag : forumMessage.getTaggedUserNames()) {
            SimpleJdbcInsert finalInsert = insert;

            Optional<User> byUsername = userDao.findByUsername(userTag);
            logger.info("searching for username with name {}", userTag);
            if (byUsername.isPresent()) {
                logger.info("found username with name {}", userTag);
                User user = byUsername.get();
                logger.info("saving tag with with name {}", userTag);
                messageTagDao.addTag(new MessageTag(user.getUsername(), forumMessage.getId()));
            } else {
                logger.info("inexistent with name {}", userTag);
                throw new InvalidParamException("inexistent user " + userTag);
            }
        }

        logger.info("Message added with id {}", forumMessage.getId());

        return forumMessage;
    }

    @Override
    @TransactionalWithRollback
    public void update(Message message) throws DaoException, InvalidParamException, NoMessageException {
        Optional<Message> oldMessageOptional = findById(message.getId());

        oldMessageOptional.orElseThrow(() -> new DaoException("Message does not exist."));
        Message oldMessage = oldMessageOptional.get();

        String SQL = "UPDATE message SET text = ? WHERE id = ?";

        if (!oldMessage.getText().equals(message.getText())) {
            try {
                if (jdbcTemplate.update(SQL,
                        message.getText(),
                        oldMessage.getId()) == 0) {
                    throw new NoMessageException("Bad id");
                }
            } catch (DataAccessException e) {
                logger.warn(e.getMessage());
                throw new DaoException("cannot update message text");
            }
        }

        List<String> messageOldTagList = oldMessage.getTaggedUserNames();
        List<String> messageNewTagList = message.getTaggedUserNames();

        List<String> messageOldTagListClone = new ArrayList<>(messageOldTagList);
        List<String> messageNewTagListClone = new ArrayList<>(messageNewTagList);

        messageOldTagList.removeAll(messageNewTagList); //users to deleteElasticsearchEntry
        messageNewTagListClone.removeAll(messageOldTagListClone); //users to add

        logger.info("update: adding new tags for message {}", message.getId());
        for (String userName : messageNewTagListClone) {
            logger.info("update: search for userName {}", userName);
            Optional<User> byUsername = userDao.findByUsername(userName);
            if (byUsername.isPresent()) {
                User user = byUsername.get();
                logger.info("update: add tag {}", userName);
                messageTagDao.addTag(new MessageTag(user.getUsername(),
                        message.getId()));
            } else {
                logger.info("inexistent with name {}", userName);
                throw new InvalidParamException("inexistent user " + userName);
            }
        }

        logger.info("update: deleting removed tags for message {}", message.getId());
        for (String userName : messageOldTagList) {
            logger.info("update: searching for userName {}", userName);
            Optional<User> byUsername = userDao.findByUsername(userName);
            if (byUsername.isPresent()) {
                User user = byUsername.get();
                logger.info("update: deleting tag {}", userName);
                messageTagDao.deleteTag(new MessageTag(user.getUsername(),
                        message.getId()));
            }
        }

        List<Integer> messageImageIdsList = imageDao.findByMessageId(message.getId()).stream()
                .map(image -> image.getId())
                .collect(Collectors.toList());

        List<Integer> currentImageIdsList = message.getImages().stream()
                .filter(image -> (image.getId() != null))
                .map(image -> image.getId())
                .collect(Collectors.toList());

        List<Image> imagesToAdd = message.getImages().stream()
                .filter(image -> image.getId() == null)
                .map(image ->
                {
                    image.setMessageId(message.getId());
                    return image;
                })
                .collect(Collectors.toList());

        if (messageImageIdsList.size() + imagesToAdd.size() > Constants.MAX_IMAGES) {
            throw new InvalidParamException("Image limit exceeded");
        }

        messageImageIdsList.removeAll(currentImageIdsList);

        for (Integer imageId : messageImageIdsList) {
            imageDao.remove(imageId);
        }

        for (Image image : imagesToAdd) {
            imageDao.save(image);
        }

        logger.info("Updated message with ID ={}", message.getId());
    }

    @Override
    public void remove(int id) throws DaoException, NoMessageException {
        String SQL = "DELETE FROM message WHERE id = ?";
        try {
            if (jdbcTemplate.update(SQL, new Object[]{id}) == 0) {
                throw new NoMessageException("Bad id");
            }
        } catch (DataAccessException e) {
            logger.warn(e.getMessage());
            throw new DaoException("cannot remove message with id " + id);
        }
        logger.info("Deleted Message with ID = {}", id);
    }

    @Override
    public Optional<Message> findById(int id) throws DaoException {
        return findMessage(id, Optional.empty());
    }

    @Override
    public Optional<Message> getMessageFromThread(int threadId, int msgId) throws DaoException {
        return findMessage(msgId, Optional.of(threadId));
    }

    private Optional<Message> findMessage(int id, Optional<Integer> threadId) throws DaoException {
        if (threadId.isPresent()) {
            logger.info("find message with id {} and thread id {}", id, threadId);
        } else {
            logger.info("find message with id {}", id);
        }

        Map<String, String> queries = getFindByIdQueries();

        formatQueries(queries, threadId);

        //get message without dependencies
        logger.info("get message without dependencies");
        List<Message> messages = null;
        try {
            messages = jdbcTemplate.query(queries.get("all"),
                    forumMessageMapper, id);
        } catch (DataAccessException e) {
            logger.warn(e.getMessage());
            throw new DaoException("cannot retrieve message without dependencies", e);
        }


        if (messages.isEmpty()) {
            return Optional.empty();
        }

        //retrieve upvotes dynamically
        logger.info("retrieve upvotes dynamically");

        Map<Integer, Integer> upvotes;
        try {
            upvotes = jdbcTemplate.query(
                    queries.get("upVote"),
                    upvoteExtractor,
                    id
            );
        } catch (DataAccessException e) {
            logger.warn(e.getMessage());
            throw new DaoException("cannot retrieve upvote", e);
        }

        //retrieve downvotes dynamically
        logger.info("retrieve downvotes dynamically");
        Map<Integer, Integer> downvotes = null;
        try {
            downvotes = jdbcTemplate.query(
                    queries.get("downVote"),
                    downvoteExtractor,
                    id
            );
        } catch (DataAccessException e) {
            logger.warn(e.getMessage());
            throw new DaoException("cannot retrieve downvote", e);
        }

        //get tags
        logger.info("retrieve tags");
        Map<Integer, List<String>> tags = null;
        try {
            tags = jdbcTemplate.query(
                    queries.get("tags"),
                    userTagExtractor,
                    id
            );
        } catch (DataAccessException e) {
            logger.warn(e.getMessage());
            throw new DaoException("cannot retrieve tags", e);
        }

        Message message = messages.get(0);

        constructMessageWithDependencies(
                message,
                tags.get(message.getId()),
                imageDao.findByMessageId(message.getId()),
                upvotes.get(message.getId()),
                downvotes.get(message.getId()));

        return Optional.of(message);
    }

    private List<Message> findAllMessages(Optional<Integer> threadId, Optional<String> sortBy,
                                          Optional<String> order, Optional<Integer> limit,
                                          Optional<Integer> offset)
            throws DaoException, InvalidParamException {
        Map<String, String> queries = getFindAllQueries();

        String ordering = "";
        Map<String, Object> objectMap = new HashMap<>();

        if (limit.isPresent()) {
            Integer limitNumber = limit.get();
            logger.info("limit is present: {}", limitNumber);
            if (limitNumber < 0) {
                logger.info("limit was smaller than 0 => exception");
                throw new InvalidParamException("Limit cannot be smaller than 0");
            }
        }

        if (offset.isPresent()) {
            Integer offsetNumber = offset.get();
            logger.info("offset is present: {}", offsetNumber);
            if (offsetNumber < 0) {
                logger.info("offset was smaller than 0 => exception");
                throw new InvalidParamException("offset cannot be smaller than 0");
            }
        }

        if (order.isPresent()) {
            ordering = order.get();
            if (!ordering.equals("asc") && !ordering.equals("desc")) {
                logger.info("order can be 'desc' or 'asc' but was {}", ordering);
                throw new InvalidParamException("order can be 'desc' or 'asc' ");
            }
        }

        if (order.isPresent() && order.get().toLowerCase().equals("desc")) {
            ordering = "desc";
        }

        logger.info("order was {}", ordering);

        formatQueries(queries, threadId);

        //to not query all the rows from the db, first the indexes of the result need to be returned
        //this will be needed for pagination
        List<Integer> indexes = calculateIndexes(queries, sortBy, ordering, limit, offset);

        if (indexes != null) {
            if (indexes.isEmpty()) {
                return new ArrayList<>();
            }
            //add where in clause to all the clauses
            queries.replaceAll((k, v) -> v.replace("{1}", "and m.id in (:indexes)"));
            objectMap.put("indexes", indexes);
        } else {
            queries.replaceAll((k, v) -> v.replace("{1}", ""));
        }

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate.getDataSource());

        //retrieve upvotes dynamically
        logger.info("retrieve upvotes");
        Map<Integer, Integer> upvotes = null;
        try {
            upvotes = namedParameterJdbcTemplate.query(
                    queries.get("upVote"),
                    objectMap,
                    upvoteExtractor
            );
        } catch (DataAccessException e) {
            logger.warn(e.getMessage());
            throw new DaoException("cannot retrieve upvote", e);
        }

        //retrieve downvotes dynamically
        logger.info("retrieve downvotes");
        Map<Integer, Integer> downvotes = null;
        try {
            downvotes = namedParameterJdbcTemplate.query(
                    queries.get("downVote"),
                    objectMap,
                    downvoteExtractor
            );
        } catch (DataAccessException e) {
            logger.warn(e.getMessage());
            throw new DaoException("cannot retrieve downvotes", e);
        }

        //get tags
        logger.info("retrieve tags");
        Map<Integer, List<String>> tags = null;
        try {
            tags = namedParameterJdbcTemplate.query(
                    queries.get("tags"),
                    objectMap,
                    userTagExtractor
            );
        } catch (DataAccessException e) {
            logger.warn(e.getMessage());
            throw new DaoException("cannot retrieve tags for messages", e);
        }

        //get images
        logger.info("retrieve images");
        Map<Integer, List<Image>> images = null;
        try {
            images = namedParameterJdbcTemplate.query(
                    queries.get("images"),
                    objectMap,
                    imagesExtractor
            );
        } catch (DataAccessException e) {
            logger.warn(e.getMessage());
            throw new DaoException("cannot retrieve images for messages", e);
        }

        //get all messages without dependencies
        logger.info("retrieve messages");
        Map<Integer, Message> messages = null;
        try {
            messages = namedParameterJdbcTemplate.query(
                    queries.get("all"),
                    objectMap,
                    forumMessageExtractor);
        } catch (DataAccessException e) {
            logger.warn(e.getMessage());
            throw new DaoException("cannot retrieve messages", e);
        }

        //construct messages
        if (indexes != null) {
            logger.info("construct messages with indexes");
            List<Message> result = new ArrayList<>();
            for (Integer index : indexes) {
                constructMessageWithDependencies(
                        messages.get(index),
                        tags.get(index),
                        images.get(index),
                        upvotes.get(index),
                        downvotes.get(index));

                result.add(messages.get(index));
            }
            return result;
        } else {
            logger.info("construct all the messages");
            for (Message message : messages.values()) {
                constructMessageWithDependencies(
                        message,
                        tags.get(message.getId()),
                        images.get(message.getId()),
                        upvotes.get(message.getId()),
                        downvotes.get(message.getId()));
            }

            return new ArrayList(messages.values());
        }
    }

    private Map<String, String> getFindByIdQueries() {
        Map<String, String> queries = new HashMap<>();

        queries.put("all", "select * from message where id=? %s");
        queries.put("upVote", "select m.id ,count(type) as up_count from message m join rating r on m.id=r.message_id\n" +
                "where type = 'upVote' and m.id=? %s group by id");
        queries.put("downVote", "select m.id ,count(type) as down_count from message m join rating r on m.id=r.message_id\n" +
                "where type = 'downVote' and m.id=? %s group by id");
        queries.put("tags", "SELECT m.id, mtu.user\n" +
                "                    FROM message m\n" +
                "                    JOIN message_tag_user mtu\n" +
                "                        on mtu.message_id = m.id\n" +
                "                   where m.id = ? %s");
        return queries;
    }

    private Map<String, String> getFindAllQueries() {
        Map<String, String> queries = new HashMap<>();

        queries.put("all", "select * from message m where true %s {1}");

        queries.put("upVote", "select mm.id, mm2.up_count from message mm left join (\n" +
                "select m.id ,count(type) as up_count from message m join rating r on m.id=r.message_id\n" +
                "where type = 'upVote' {1}  group by m.id) mm2 on mm.id=mm2.id where true %s");

        queries.put("downVote", "select mm.id, mm2.down_count from message mm left join (\n" +
                "select m.id ,count(type) as down_count from message m join rating r on m.id=r.message_id\n" +
                "where type = 'downVote' {1}  group by m.id) mm2 on mm.id=mm2.id where true %s");
        queries.put("tags", "SELECT m.id, mtu.user\n" +
                "                    FROM message m\n" +
                "                    JOIN message_tag_user mtu\n" +
                "                        on mtu.message_id = m.id\n" +
                "                   where true %s {1}");
        queries.put("images", "SELECT m.id as msg_id, i.img_blob, i.message_id, i.id \n" +
                "                    FROM message m\n" +
                "                    JOIN image i\n" +
                "                        on i.message_id = m.id where true %s {1}");

        return queries;
    }

    private List<Integer> calculateIndexes(Map<String, String> queries,
                                           Optional<String> sortBy,
                                           String ordering, Optional<Integer> limit,
                                           Optional<Integer> offset) throws InvalidParamException,
            DaoException {
        List<Integer> indexes = null;

        if (sortBy.isPresent()) {
            logger.info("calculate indexes for pagination, sorting by {} and ordering {}",
                    sortBy, ordering);
        }

        StringBuilder query;
        if (sortBy.isPresent()) {
            String field = sortBy.get().toLowerCase();
            switch (field) {
                case "upvote":
                    query = new StringBuilder(queries.get("upVote").replace("{1}", ""))
                            .append(" order by up_count ").append(ordering);
                    addLimitAndOffsetToQuery(query, limit, offset);
                    try {
                        indexes = new ArrayList<>(jdbcTemplate.query(query.toString(),
                                new UpvoteExtractor()
                        ).keySet());
                    } catch (DataAccessException e) {
                        logger.warn(e.getMessage());
                        throw new DaoException("cannot get index for upvote");
                    }
                    break;
                case "downvote":
                    query = new StringBuilder(queries.get("downVote").replace("{1}", ""))
                            .append(" order by down_count ").append(ordering);
                    addLimitAndOffsetToQuery(query, limit, offset);
                    try {
                        indexes = new ArrayList<>(jdbcTemplate.query(
                                query.toString(),
                                downvoteExtractor
                        ).keySet());
                    } catch (DataAccessException e) {
                        logger.warn(e.getMessage());
                        throw new DaoException("cannot get index for downVote");
                    }
                    break;
                case "text":
                    query = new StringBuilder(queries.get("all").replace("{1}", ""))
                            .append(" order by LOWER(text) ").append(ordering);
                    addLimitAndOffsetToQuery(query, limit, offset);
                    try {
                        indexes = getQueryIndexes(query.toString());
                    } catch (DataAccessException e) {
                        logger.warn(e.getMessage());
                        throw new DaoException("cannot get index for text");
                    }
                    break;
                case "date":
                case "user":
                    query = new StringBuilder(queries.get("all").replace("{1}", ""))
                            .append(" order by " + field + " ").append(ordering);
                    addLimitAndOffsetToQuery(query, limit, offset);
                    try {
                        indexes = getQueryIndexes(query.toString());
                    } catch (DataAccessException e) {
                        logger.warn(e.getMessage());
                        throw new DaoException("cannot get index for " + field);
                    }
                    break;
                default:
                    throw new InvalidParamException("invalid sortBy field");
            }
        } else {
            //no sorting but still need to do pagination
            logger.info("no sortBy present, doing only pagination");
            query = new StringBuilder(queries.get("all").replace("{1}", ""));
            addLimitAndOffsetToQuery(query,
                    limit, offset);
            try {
                indexes = getQueryIndexes(query.toString());
            } catch (DataAccessException e) {
                logger.warn(e.getMessage());
                throw new DaoException("cannot get index for pagination");
            }
        }

        if (indexes != null) {
            logger.info("returned indexes {}", indexes.stream().
                    map(i -> Integer.toString(i)).collect(Collectors.joining(",")));
        }
        return indexes;
    }

    private void addLimitAndOffsetToQuery(StringBuilder query, Optional<Integer> limit,
                                          Optional<Integer> offset) {
        if (limit.isPresent()) {
            query.append(" limit ").append(limit.get());
        }
        if (offset.isPresent()) {
            query.append(" offset ").append(offset.get());
        }
    }

    private List<Integer> getQueryIndexes(String query) throws DataAccessException {
        return jdbcTemplate.query(query,
                forumMessageMapper)
                .stream()
                .map(m -> m.getId())
                .collect(Collectors.toList());
    }

    private void formatQueries(Map<String, String> queries, Optional<Integer> threadId) {
        if (threadId.isPresent()) {
            queries.replaceAll((k, v) -> {
                String temp = String.format(v, " and thread_id=%d");
                return String.format(temp, threadId.get());
            });
        } else {
            queries.replaceAll((k, v) -> String.format(v, ""));
        }
    }

    private void constructMessageWithDependencies(Message message,
                                                  List<String> tags, final List<Image> images,
                                                  final Integer upvote, final Integer downvote) {

        logger.info("construct message with id={} with dependencies", message.getId());

        if (images != null) {
            message.setImages(images);
        }

        if (tags != null) {
            message.setTaggedUserNames(tags);
        }

        if (upvote != null) {
            message.setUpCounter(upvote);
        }

        if (downvote != null) {
            message.setDownCounter(downvote);
        }
    }
}
