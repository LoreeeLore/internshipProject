package com.studlabs.dao.mappers;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PrivacyUserExtractor implements ResultSetExtractor<List<String>> {

    @Override
    public List<String> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<String> users = new ArrayList<>();

        while (rs.next()) {
            users.add(rs.getString("user"));
        }

        return users;
    }
}
