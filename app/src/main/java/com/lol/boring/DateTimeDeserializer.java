package com.lol.boring;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.lang.reflect.Type;

public class DateTimeDeserializer implements JsonDeserializer<DateTime> {
    @Override
    public DateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        String datetime = jsonElement.getAsString();
        return DateTime.parse(datetime, DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ"));
    }
}
