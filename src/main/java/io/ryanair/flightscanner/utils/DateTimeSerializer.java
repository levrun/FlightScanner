package io.ryanair.flightscanner.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeSerializer extends JsonSerializer<LocalDateTime> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @Override
    public void serialize(LocalDateTime value,
                          JsonGenerator generator,
                          SerializerProvider serializers) throws IOException {

        generator.writeString(DATE_TIME_FORMATTER.format(value));
    }

}
