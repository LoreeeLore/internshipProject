package com.studlabs.quiz.repository;

import com.studlabs.quiz.exception.*;

import javax.sql.rowset.serial.*;
import java.sql.*;
import java.util.*;

public class ImageConverter {

    public static Blob convertStringBase64ToBlob(String image) throws ConvertBlobException {
        byte[] imageByte = Base64.getDecoder().decode(image);

        try {
            return new SerialBlob(imageByte);
        } catch (SQLException e) {
            throw new ConvertBlobException("Can't convert image", e);
        }
    }
}
