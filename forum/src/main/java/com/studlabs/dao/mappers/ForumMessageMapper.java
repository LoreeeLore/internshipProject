package com.studlabs.dao.mappers;

import com.studlabs.bll.model.Message;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ForumMessageMapper implements RowMapper<Message> {

    @Override
    public Message mapRow(ResultSet resultSet, int i) throws SQLException {
        return new Message(resultSet.getInt("id"),
                resultSet.getInt("thread_id"),
                resultSet.getString("user"),
                resultSet.getString("text"),
                resultSet.getTimestamp("date").toLocalDateTime(),
                0,
                0,
                new ArrayList<>(),
                new ArrayList<>());
    }

}
