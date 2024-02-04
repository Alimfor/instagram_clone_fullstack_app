package com.gaziyev.microinstaclone.feedservice.util;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class InstantDateTypeAdapter implements JsonSerializer<Instant>, JsonDeserializer<Instant> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

    @Override
    public JsonElement serialize(final Instant instant, final Type typeOfSrc,
                                 final JsonSerializationContext context) {
        return new JsonPrimitive(formatter.format(instant));
    }

    @Override
    public Instant deserialize(final JsonElement json, final Type typeOfT,
                               final JsonDeserializationContext context) throws JsonParseException {
        return Instant.from(formatter.parse(json.getAsString()));
    }
}
