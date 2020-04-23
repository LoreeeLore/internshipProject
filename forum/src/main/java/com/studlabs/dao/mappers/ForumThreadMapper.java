package com.studlabs.dao.mappers;

import com.studlabs.bll.model.ForumThread;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ForumThreadMapper implements RowMapper<ForumThread> {

    @Override
    public ForumThread mapRow(ResultSet rs, int rowNum) throws SQLException {
        ForumThread thread = new ForumThread(rs.getInt("id"),
                rs.getString("category"),
                rs.getString("access"),
                rs.getString("title"),
                rs.getTimestamp("date").toLocalDateTime());

        thread.setState(rs.getString("state"));
        thread.setUser(rs.getString("user"));

        return thread;
    }
}
