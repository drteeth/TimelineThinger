package com.lol.boring;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;

public class GsonFactory {

    public static Gson getGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Event.class, new EventDeserializer());
        builder.registerTypeAdapter(DateTime.class, new DateTimeDeserializer());
        return builder.create();
    }
}
