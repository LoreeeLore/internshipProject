package com.studlabs.dao.mappers;

import com.studlabs.bll.model.Image;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImagesExtractor implements ResultSetExtractor<Map<Integer, List<Image>>> {

    @Override
    public Map<Integer, List<Image>> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, List<Image>> images = new HashMap<>();

        while (rs.next()) {
            int messageId = rs.getInt("msg_id");

            if (!images.containsKey(messageId)) {
                images.put(messageId, new ArrayList<>());
            }

            images.get(messageId).add(new ImageMapper().mapRow(rs, 0));
        }

        return images;
    }
}
