package com.studlabs.dao.mappers;

import com.studlabs.bll.model.Rating;
import com.studlabs.bll.model.RatingType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RatingMapper implements RowMapper<Rating> {

    @Override
    public Rating mapRow(ResultSet resultSet, int i) throws SQLException {
        return new Rating(resultSet.getInt("message_id"),
                resultSet.getString("user"),
                RatingType.fromString(resultSet.getString("type")));
    }

}
