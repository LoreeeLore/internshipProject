package com.studlabs.dao.mappers;

import com.studlabs.bll.model.Message;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ForumMessageExtractor implements ResultSetExtractor<Map<Integer, Message>> {

    @Override
    public Map<Integer, Message> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, Message> messages = new HashMap<>();

        while (rs.next()) {
            messages.put(rs.getInt("id"),
                    new Message(rs.getInt("id"),
                            rs.getInt("thread_id"),
                            rs.getString("user"),
                            rs.getString("text"),
                            rs.getTimestamp("date").toLocalDateTime(),
                            0,
                            0,
                            new ArrayList<>(),
                            new ArrayList<>()));

        }

        return messages;
    }
}
