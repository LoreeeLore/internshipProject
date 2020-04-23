package com.studlabs.dao.mappers;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class UpvoteExtractor implements ResultSetExtractor<Map<Integer, Integer>> {

    @Override
    public Map<Integer, Integer> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, Integer> upvotes = new LinkedHashMap<>();

        while (rs.next()) {
            upvotes.put(rs.getInt("id"), rs.getInt("up_count"));
        }

        return upvotes;
    }
}


