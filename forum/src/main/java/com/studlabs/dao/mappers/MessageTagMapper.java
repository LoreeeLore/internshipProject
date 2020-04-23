package com.studlabs.dao.mappers;


import com.studlabs.bll.model.MessageTag;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MessageTagMapper implements RowMapper<MessageTag> {

    @Override
    public MessageTag mapRow(ResultSet resultSet, int i) throws SQLException {
        return new MessageTag(resultSet.getString("user"),
                resultSet.getInt("message_id"));
    }
}
