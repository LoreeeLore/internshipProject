package com.studlabs.dao;

import com.studlabs.bll.exceptions.DaoException;
import com.studlabs.bll.exceptions.InvalidParamException;
import com.studlabs.bll.exceptions.NoThreadException;
import com.studlabs.bll.model.ForumThread;
import com.studlabs.bll.model.Tag;
import com.studlabs.dao.mappers.PrivacyUserExtractor;
import com.studlabs.dao.mappers.ThreadTagExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ThreadDaoImpl implements ThreadDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TagDaoImpl tagDao;

    private static final Logger logger = LoggerFactory.getLogger(ThreadDaoImpl.class);

    @Override
    public List<ForumThread> findAllOpen() throws DaoException {
        String sql = "SELECT th.id,th.state,th.user, th.category, th.access, th.title, tn.tag_name, tn.id, th.date FROM tag AS tn\n" +
                "INNER JOIN thread_tag AS tg ON tn.id=tg.tag_id\n" +
                "RIGHT JOIN thread AS th ON th.id=tg.thread_id where th.state='open'";

        return getThreads(sql);
    }

    private List<ForumThread> getThreads(String sql) throws DaoException {
        Map<ForumThread, List<Tag>> tagsMap;

        logger.info("Getting all threads");

        try {
            tagsMap = jdbcTemplate.query(sql,
                    new ThreadTagExtractor());
        } catch (DataAccessException e) {
            logger.info(e.getMessage());
            throw new DaoException("Database exception", e);
        }

        tagsMap.keySet()
                .forEach(thread -> thread.setTags(tagsMap.get(thread).stream()
                        .filter(tag -> tag.getTagName() != null)
                        .map(Tag::getTagName)
                        .collect(Collectors.toList())));
        return new ArrayList<>(tagsMap.keySet());
    }

    @Override
    public List<ForumThread> findAll() throws DaoException {
        String sql = "SELECT th.id, th.state,th.user, th.category, th.access, th.title, tn.tag_name, tn.id, th.date FROM tag AS tn\n" +
                "INNER JOIN thread_tag AS tg ON tn.id=tg.tag_id\n" +
                "RIGHT JOIN thread AS th ON th.id=tg.thread_id";

        return getThreads(sql);
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public ForumThread save(ForumThread forumThread) throws DaoException {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
        insert.setTableName("thread");
        insert.usingGeneratedKeyColumns("id");
        Map<String, Object> map = new HashMap<>();
        map.put("access", forumThread.getAccess());
        map.put("category", forumThread.getCategory());
        map.put("title", forumThread.getTitle());

        if (forumThread.getUser() != null) {
            map.put("user", forumThread.getUser());
        }


        if (forumThread.getState() == null) {
            forumThread.setState("open");
        }

        map.put("state", forumThread.getState());

        if (forumThread.getDate() != null) {
            map.put("date", forumThread.getDate());
        } else {
            logger.info("save: date not present in message, setting it to current date");
            forumThread.setDate(LocalDateTime.now());
            map.put("date", forumThread.getDate());
        }

        try {
            int id = insert.executeAndReturnKey(map).intValue();
            forumThread.setId(id);
            logger.info("Persisted thread with id {}", forumThread.getId());
        } catch (DataAccessException e) {
            logger.info(e.getMessage());
            throw new DaoException("Database exception", e);
        }

        List<String> existingTags = tagDao.find(forumThread.getTags()).stream()
                .map(Tag::getTagName)
                .collect(Collectors.toList());
        List<String> currentTags = new ArrayList<>(forumThread.getTags());
        currentTags.removeAll(existingTags);

        for (String currentTag : currentTags) {
            tagDao.save(new Tag(currentTag));
        }

        for (String tag : forumThread.getTags()) {
            tagDao.saveTagToThread(tagDao.getTag(tag).get().getId(), forumThread.getId());
        }
        return forumThread;
    }


    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void update(ForumThread thread) throws DaoException, NoThreadException {
        Optional<ForumThread> optionalForumThread = findById(thread.getId());
        optionalForumThread.orElseThrow(() -> new NoThreadException("Thread cannot be found"));

        String SQL = "update thread set category = ?, title = ?, access = ? where id = ?";

        SqlUpdate sqlUpdate = new SqlUpdate(jdbcTemplate.getDataSource(), SQL);
        sqlUpdate.declareParameter(new SqlParameter("category", Types.VARCHAR));
        sqlUpdate.declareParameter(new SqlParameter("title", Types.VARCHAR));
        sqlUpdate.declareParameter(new SqlParameter("access", Types.VARCHAR));
        sqlUpdate.declareParameter(new SqlParameter("id", Types.INTEGER));
        sqlUpdate.compile();

        try {
            sqlUpdate.update(thread.getCategory(), thread.getTitle(), thread.getAccess(), thread.getId());
        } catch (DataAccessException e) {
            logger.info(e.getMessage());
            throw new DaoException("Database exception", e);
        }

        List<String> currentTags = new ArrayList<>(thread.getTags());
        List<String> existingTagsForThread = tagDao.getTagsByThreadId(thread.getId()).stream()
                .map(Tag::getTagName)
                .collect(Collectors.toList());
        List<String> existingTags = tagDao.find(thread.getTags()).stream()
                .map(Tag::getTagName)
                .collect(Collectors.toList());

        currentTags.removeAll(existingTagsForThread); //tags to be added
        existingTagsForThread.removeAll(thread.getTags()); // tags to be remove

        List<String> tagsToAddToDb = new ArrayList<>(currentTags);
        tagsToAddToDb.removeAll(existingTags);

        for (String tag : tagsToAddToDb) {
            tagDao.save(new Tag(tag));
        }

        for (String tag : existingTagsForThread) {
            tagDao.deleteTagFromThread(tagDao.getTag(tag).get().getId(), thread.getId());
        }

        for (String currentTag : currentTags) {
            tagDao.saveTagToThread(tagDao.getTag(currentTag).get().getId(), thread.getId());
        }

        logger.info("Updated Thread with ID = {}", thread.getId());
    }

    @Override
    public void remove(int id) throws DaoException, NoThreadException {
        String SQL = "DELETE FROM thread WHERE id = ?";
        if (jdbcTemplate.update(SQL, new Object[]{id}) == 0) {
            logger.info("Thread with id {} cannot be found", id);
            throw new NoThreadException("Thread cannot be found");
        }
        logger.info("Deleted Thread with ID = " + id);
    }

    @Override
    public Optional<ForumThread> findById(int id) throws DaoException, NoThreadException {
        Map<ForumThread, List<Tag>> tagsMap;

        String sql = "SELECT th.id,th.user, th.state, th.category, th.access, th.title, tn.tag_name, tn.id, th.date FROM tag AS tn\n" +
                "INNER JOIN thread_tag AS tg ON tn.id=tg.tag_id\n" +
                "RIGHT JOIN thread AS th ON th.id=tg.thread_id\n" +
                "WHERE th.id = ?";

        logger.info("Getting thread with id {}", id);

        try {
            tagsMap = jdbcTemplate.query(sql, new ThreadTagExtractor(), id);
        } catch (DataAccessException e) {
            logger.warn("Database error: {}", e.getMessage());
            throw new DaoException("Database error", e);
        }

        if (tagsMap.size() == 0) {
            logger.info("Thread with id {} cannot be found", id);
            throw new NoThreadException("Thread cannot be found");
        }

        ForumThread thread = new ArrayList<>(tagsMap.keySet()).get(0);

        thread.setTags(tagsMap.get(thread).stream()
                .filter(tag -> tag.getTagName() != null)
                .map(Tag::getTagName)
                .collect(Collectors.toList()));

        return Optional.of(thread);
    }

    @Override
    public List<ForumThread> filterThreads(Optional<String> category,
                                           Optional<List<String>> tags,
                                           Optional<String> sortBy,
                                           Optional<String> order,
                                           Optional<Integer> limit,
                                           Optional<Integer> offset) throws DaoException, InvalidParamException {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate.getDataSource());


        if (!category.isPresent() && !tags.isPresent() && !sortBy.isPresent() && !order.isPresent() && !limit.isPresent() && !offset.isPresent()) {
            return findAll();
        }

        List<String> sqlQueries = new ArrayList<>();
        List<String> paginationQueries = new ArrayList<>();
        Map<String, Object> objectMap = new HashMap<>();

        String sql = "SELECT id FROM thread WHERE";

        if (category.isPresent()) {
            sqlQueries.add(" category=:category");
            objectMap.put("category", category.get());
            logger.info("Search after category: {}", category.get());
        }

        if (tags.isPresent()) {
            sqlQueries.add(" id in (\n" +
                    "SELECT thread_id FROM thread_tag where tag_id in (\n" +
                    "select id from tag where tag_name in (:list))\n" +
                    "group by thread_id having count(*)=:list_size)\n");
            objectMap.put("list", tags.get());
            objectMap.put("list_size", tags.get().size());
            logger.info("Search after tags: {}", tags.get());
        }

        if (limit.isPresent()) {
            logger.info("Limit is present with value: {}", limit.get());
            if (limit.get() < 0) {
                logger.info("Limit less than 0");
                throw new InvalidParamException("Limit can't be negative");
            }

            paginationQueries.add(String.format(" LIMIT %d ", limit.get()));
            logger.info("Limit set to: {}", limit.get());
        }

        if (offset.isPresent()) {
            logger.info("Offset is present with value: {}", offset.get());
            if (offset.get() < 0) {
                logger.info("Offset less than 0");
                throw new InvalidParamException("Offset can't be negative");
            }

            if (!limit.isPresent()) {
                logger.info("Offset present without limit. Set maximum limit");
                paginationQueries.add(" LIMIT 1000000");
            }

            paginationQueries.add(String.format(" OFFSET %d", offset.get()));
            logger.info("Offset set to: {}", offset.get());
        }

        if (sqlQueries.size() > 0) {
            sql += sqlQueries.get(0);

            for (int i = 1; i < sqlQueries.size(); ++i) {
                sql += " AND " + sqlQueries.remove(i);
            }
        } else {
            sql = sql.replace(" WHERE", "");
        }

        for (String query : paginationQueries) {
            sql += query;
        }

        List<Integer> indexs = new ArrayList<>();

        try {
            indexs = namedParameterJdbcTemplate.query(sql, objectMap, (resultSet, i) ->
                    resultSet.getInt("id")
            );
        } catch (DataAccessException e) {
            throw new DaoException("Database error", e);
        }

        sql = "SELECT th.id, th.state,th.user, th.category, th.access, th.title, tn.tag_name, tn.id, th.date FROM tag AS tn\n" +
                "INNER JOIN thread_tag AS tg ON tn.id=tg.tag_id\n" +
                "RIGHT JOIN thread AS th ON th.id=tg.thread_id\n";

        if (indexs.size() > 0) {
            sql += " WHERE th.id in (:idList)";

            objectMap.clear();
            objectMap.put("idList", indexs);
        } else {
            logger.info("No result found");
            return new ArrayList<>();
        }

        List<String> sortableColumns = Arrays.asList("category", "access", "title", "date");

        if (sortBy.isPresent()) {
            if (!sortableColumns.contains(sortBy.get())) {
                logger.info("Invalid sort field: {}", sortBy.get());
                throw new InvalidParamException(String.format("Cannot sort thread by field %s", sortBy.get()));
            }

            if (order.isPresent() && !order.get().equals("asc") && !order.get().equals("desc")) {
                logger.info("Invalid sort order: {}", order.get());
                throw new InvalidParamException(String.format("%s is not a valid sorting order", order.get()));
            }

            String field = sortBy.get();
            if (sortableColumns.contains(field)) {
                if (field.equals("date")) {
                    sql += " order by date";
                } else {
                    sql += " order by LOWER(" + field + ")";//case insensitive sorting
                }

                String ordering = " asc";
                if (order.isPresent() && order.get().toLowerCase().equals("desc")) {
                    ordering = " desc";
                }

                sql += ordering;
            }
        }

        Map<ForumThread, List<Tag>> tagsMap;

        try {
            tagsMap = namedParameterJdbcTemplate.query(sql, objectMap, new ThreadTagExtractor());
        } catch (DataAccessException e) {
            logger.info(e.getMessage());
            throw new DaoException("Database error", e);
        }

        tagsMap.keySet()
                .forEach(thread -> thread.setTags(tagsMap.get(thread).stream()
                        .filter(tag -> tag.getTagName() != null)
                        .map(Tag::getTagName)
                        .collect(Collectors.toList())));
        return new ArrayList<>(tagsMap.keySet());
    }

    @Override
    public List<String> getUsers(int id) throws DaoException, NoThreadException, InvalidParamException {
        Optional<ForumThread> optionalForumThread = findById(id);
        optionalForumThread.orElseThrow(() -> new NoThreadException("Thread cannot be found"));

        if (!optionalForumThread.get().getAccess().equals("private")) {
            logger.info("Getting all users for public thread");
            throw new InvalidParamException("privacy only exists for private threads");
        }

        String sql = "SELECT p.user FROM thread_privacy_user p join thread th on th.id=p.thread_id where th.id=?";

        logger.info("Getting all users");

        try {
            return jdbcTemplate.query(sql,
                    new PrivacyUserExtractor(), id);
        } catch (DataAccessException e) {
            logger.info(e.getMessage());
            throw new DaoException("Database exception", e);
        }
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void addUsers(int id, List<String> users) throws DaoException,
            NoThreadException, InvalidParamException {
        Optional<ForumThread> optionalForumThread = findById(id);
        optionalForumThread.orElseThrow(() -> new NoThreadException("Thread cannot be found"));

        if (!optionalForumThread.get().getAccess().equals("private")) {
            logger.info("adding users for public thread");
            throw new InvalidParamException("privacy only exists for private threads");
        }

        logger.info("adding users");

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
        insert.setTableName("thread_privacy_user");
        Map<String, Object> map = new HashMap<>();
        map.put("thread_id", id);

        for (String user : users) {
            map.put("user", user);

            try {
                insert.execute(map);
            } catch (DataIntegrityViolationException e) {
                logger.warn(e.getMessage());
                throw new InvalidParamException("cannot add user " + user + "- bad parameters");
            } catch (DataAccessException e) {
                logger.warn(e.getMessage());
                throw new DaoException("cannot add user");
            }
        }
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void removeUsers(int id, List<String> users) throws DaoException,
            NoThreadException, InvalidParamException {
        Optional<ForumThread> optionalForumThread = findById(id);
        optionalForumThread.orElseThrow(() -> new NoThreadException("Thread cannot be found"));

        if (!optionalForumThread.get().getAccess().equals("private")) {
            logger.info("removing users for public thread");
            throw new InvalidParamException("privacy only exists for private threads");
        }

        logger.info("removing users");

        for (String user : users) {
            try {
                if (jdbcTemplate.update("DELETE FROM thread_privacy_user WHERE user=? AND thread_id=?",
                        user,
                        id) == 0) {
                    throw new InvalidParamException("Inexistent user");
                }
            } catch (DataAccessException e) {
                logger.warn(e.getMessage());
                throw new DaoException("cannot deleteElasticsearchEntry user " + user);
            }
        }
    }

    @Override
    public void close(int id) throws DaoException, NoThreadException {
        Optional<ForumThread> optionalForumThread = findById(id);
        optionalForumThread.orElseThrow(() -> new NoThreadException("Thread cannot be found"));

        logger.info("close thread {}", id);

        String sql = "update thread set state = 'closed' where id = ?";

        try {
            jdbcTemplate.update(sql, id);
        } catch (DataAccessException e) {
            logger.info(e.getMessage());
            throw new DaoException("Database exception", e);
        }
    }

    @Override
    public void validate(int id) throws DaoException, NoThreadException {
        Optional<ForumThread> optionalForumThread = findById(id);
        optionalForumThread.orElseThrow(() -> new NoThreadException("Thread cannot be found"));

        logger.info("validate thread {}", id);

        String sql = "update thread set state = 'validated' where id = ?";

        try {
            jdbcTemplate.update(sql, id);
        } catch (DataAccessException e) {
            logger.info(e.getMessage());
            throw new DaoException("Database exception", e);
        }
    }
}
