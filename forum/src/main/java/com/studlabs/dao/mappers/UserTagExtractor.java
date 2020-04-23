package com.studlabs.dao.mappers;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserTagExtractor implements ResultSetExtractor<Map<Integer, List<String>>> {

    @Override
    public Map<Integer, List<String>> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, List<String>> tags = new HashMap<>();

        while (rs.next()) {
            int messageId = rs.getInt("id");

            if (!tags.containsKey(messageId)) {
                tags.put(messageId, new ArrayList<>());
            }

            tags.get(messageId).add(rs.getString("user"));
        }

        return tags;
    }
}
