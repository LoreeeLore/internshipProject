package com.studlabs.dao.mappers;

import com.studlabs.bll.model.Image;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public class ImageMapper implements RowMapper<Image> {
    @Override
    public Image mapRow(ResultSet resultSet, int i) throws SQLException {
        Blob blob = resultSet.getBlob("img_blob");
        byte[] bytes = blob.getBytes(1, (int) blob.length());
        byte[] encodedBytes = Base64.getEncoder().encode(bytes);

        return new Image(resultSet.getInt("id"),
                resultSet.getInt("message_id"),
                new String(encodedBytes));
    }

}
