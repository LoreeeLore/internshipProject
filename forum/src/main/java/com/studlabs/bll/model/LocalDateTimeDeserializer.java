package com.studlabs.bll.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    private static final Logger logger = LoggerFactory.getLogger(LocalDateTimeDeserializer.class);

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctx)
            throws IOException {
        String str = p.getText();
        try {
            return LocalDateTime.parse(str, LocalDateTimeSerializer.DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            logger.warn("cannot deserialize date {}", str);
            return null;
        }
    }
}