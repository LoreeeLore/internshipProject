package com.studlabs.bll.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
    private static final Logger logger = LoggerFactory.getLogger(LocalDateTimeSerializer.class);

    static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        try {
            String s = value.format(DATE_FORMATTER);
            gen.writeString(s);
        } catch (DateTimeParseException e) {
            logger.warn("cannot serialize date {}", value);
            gen.writeString("");
        }
    }
}