package com.studlabs.dao;

import com.studlabs.bll.exceptions.DaoException;
import com.studlabs.bll.model.Tag;
import com.studlabs.dao.mappers.TagMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;


@Repository
public class TagDaoImpl implements TagDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(TagDaoImpl.class);

    @Override
    public List<Tag> findAll() throws DaoException {
        logger.info("Getting all tags");
        try {
            return jdbcTemplate.query("SELECT * FROM tag", new TagMapper());
        } catch (DataAccessException e) {
            logger.warn("Cannot get tags");
            throw new DaoException("Cannot get tags", e);
        }
    }

    @Override
    public Optional<Tag> getTag(String tagName) throws DaoException {
        List<Tag> tags = null;

        try {
            tags = jdbcTemplate.query("SELECT * FROM tag WHERE tag_name = ?", new TagMapper(), tagName);
        } catch (DataAccessException e) {
            logger.warn("Cannot get tag with name {}", tagName);
            throw new DaoException(String.format("Cannot get tag with name %s", tagName), e);
        }
        logger.info("Getting tag with name {}", tagName);
        return tags.size() > 0 ? Optional.of(tags.get(0)) : Optional.empty();
    }

    @Override
    public Tag save(Tag tag) throws DaoException {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
        insert.setTableName("tag");
        insert.usingGeneratedKeyColumns("id");
        Map<String, Object> map = new HashMap<>();
        map.put("tag_name", tag.getTagName());

        try {
            int id = insert.executeAndReturnKey(map).intValue();
            tag.setId(id);
        } catch (DataAccessException e) {
            logger.warn("Cannot save tag with name {}", tag.getTagName());
            throw new DaoException(String.format("Cannot save tag with name %s", tag.getTagName()), e);
        }

        logger.info("Persisted tag with id {}", tag.getId());

        return tag;
    }

    @Override
    public void remove(int id) throws DaoException {
        logger.info("Deleting tag with id: {}", id);
        try {
            jdbcTemplate.update("DELETE FROM tag WHERE id = ?", id);
        } catch (DataAccessException e) {
            logger.warn("Cannot deleteElasticsearchEntry tag with id {}", id);
            throw new DaoException(String.format("Cannot deleteElasticsearchEntry tag with id %s", id), e);
        }
    }

    @Override
    public void saveTagToThread(int tagId, int threadId) throws DaoException {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
        insert.setTableName("thread_tag");

        Map<String, Object> map = new HashMap<>();
        map.put("thread_id", threadId);
        map.put("tag_id", tagId);

        logger.info("Save tag with id {} to thread with id {}", tagId, threadId);
        try {
            insert.execute(map);
        } catch (DataAccessException e) {
            logger.warn("Cannot save tag with id {} to thread with id {}", tagId, threadId);
            throw new DaoException(String.format("Cannot save tag with id %s to thread with id %s", tagId, threadId), e);
        }
    }

    @Override
    public List<Tag> getTagsByThreadId(int threadId) throws DaoException {
        String sql = "SELECT tag_name, id  FROM tag INNER JOIN thread_tag ON thread_tag.tag_id = tag.id WHERE thread_tag.thread_id = ?";

        logger.info("Getting tags for thread with id: {}", threadId);
        List<Tag> tags;

        try {
            tags = jdbcTemplate.query(sql, new TagMapper(), threadId);
        } catch (DataAccessException e) {
            logger.warn("Cannot find tags from thread with id {}", threadId);
            throw new DaoException(String.format("Cannot find tags from thread with id %s", threadId), e);
        }

        if (tags == null) {
            return new ArrayList<>();
        }

        return tags;
    }

    @Override
    public void deleteTagFromThread(int tagId, int threadId) throws DaoException {
        String sql = "DELETE FROM thread_tag WHERE tag_id = ? AND thread_id = ?";
        logger.info("Delete tag with id {} from thread with id {}", tagId, threadId);

        try {
            jdbcTemplate.update(sql, tagId, threadId);
        } catch (DataAccessException e) {
            logger.warn("Cannot deleteElasticsearchEntry tag with id {} from thread with id {}", tagId, threadId);
            throw new DaoException("Cannot deleteElasticsearchEntry tag from thread", e);
        }
    }

    @Override
    public List<Tag> find(List<String> tags) throws DaoException {

        if (tags.isEmpty()) {
            logger.info("Getting tags: empty list");
            return new ArrayList<>();
        }

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate.getDataSource());

        Map<String, Object> objectMap = new HashMap<>();
        String sql = "SELECT * FROM tag WHERE tag_name in (:list)";
        objectMap.put("list", tags);

        logger.info("Getting existing tabs in date base from list {}", tags);

        try {
            return namedParameterJdbcTemplate.query(sql, objectMap, new TagMapper());
        } catch (DataAccessException e) {
            logger.warn("Cannot find tags in list {}", tags);
            throw new DaoException("Cannot find tag", e);
        }
    }

    @Override
    public void saveTagList(List<String> tags) throws DaoException {
        logger.info("Save tag list {}", tags);

        List<String> existingTags = find(tags).stream()
                .map(Tag::getTagName).collect(Collectors.toList());

        tags.removeAll(existingTags);

        for (String tag : tags) {
            save(new Tag(tag));
        }
    }

    @Override
    public void deleteTagList(List<String> tags) throws DaoException {
        logger.info("Delete tags {}", tags);

        List<Integer> existingTagsId = find(tags).stream().map(Tag::getId)
                .collect(Collectors.toList());

        for (Integer tagId : existingTagsId) {
            remove(tagId);
        }
    }
}
