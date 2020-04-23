package com.studlabs.dao.mappers;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class DownvoteExtractor implements ResultSetExtractor<Map<Integer, Integer>> {

    @Override
    public Map<Integer, Integer> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, Integer> downvotes = new LinkedHashMap<>();

        while (rs.next()) {
            downvotes.put(rs.getInt("id"), rs.getInt("down_count"));
        }

        return downvotes;
    }
}
