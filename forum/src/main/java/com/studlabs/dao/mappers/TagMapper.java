package com.studlabs.dao.mappers;

import com.studlabs.bll.model.Tag;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TagMapper implements RowMapper<Tag> {
    @Override
    public Tag mapRow(ResultSet resultSet, int i) throws SQLException {
        return new Tag(resultSet.getInt("id"),
                resultSet.getString("tag_name"));
    }
}
