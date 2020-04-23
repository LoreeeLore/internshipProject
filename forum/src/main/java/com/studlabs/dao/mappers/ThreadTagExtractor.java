package com.studlabs.dao.mappers;

import com.studlabs.bll.model.ForumThread;
import com.studlabs.bll.model.Tag;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ThreadTagExtractor implements ResultSetExtractor<Map<ForumThread, List<Tag>>> {
    @Override
    public Map<ForumThread, List<Tag>> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        Map<ForumThread, List<Tag>> resultMap = new LinkedHashMap<>();

        while (resultSet.next()) {
            ForumThread thread = new ForumThreadMapper().mapRow(resultSet, 0);
            Tag tag = new TagMapper().mapRow(resultSet, 0);

            if (!resultMap.containsKey(thread)) {
                resultMap.put(thread, new ArrayList<>());
            }

            if (tag != null) {
                resultMap.get(thread).add(tag);
            }
        }

        return resultMap;
    }
}
