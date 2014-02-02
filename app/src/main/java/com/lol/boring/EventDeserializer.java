package com.lol.boring;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.joda.time.DateTime;

import java.lang.reflect.Type;

public class EventDeserializer implements JsonDeserializer<Event> {

    @Override
    public Event deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {

        Event event = new Event();
        JsonObject jo = (JsonObject) jsonElement;
        event.id = jo.get("id").getAsLong();
        event.occursAt = (context.deserialize(jo.get("occurs_at"), DateTime.class));
        event.cardType = jo.get("card_type").getAsString();
//        event.type = CardType.getCardTypeByName(jo.get("card_type").getAsString());
//
//        switch (event.type) {
//            case FACTOID:
//                event.card = context.deserialize(jo.get("card"), Factoid.class);
//                break;
//            case ATHLETE:
//                event.card = context.deserialize(jo.get("card"), Athlete.class);
//                break;
//            case ADUNIT:
//                event.card = context.deserialize(jo.get("card"), Adunit.class);
//                break;
//            case VIDEO:
//                event.card = context.deserialize(jo.get("card"), Video.class);
//                break;
//            case TRIVIUM:
//                event.card = context.deserialize(jo.get("card"), Trivium.class);
//                break;
//            case POLL:
//                event.card = context.deserialize(jo.get("card"), Poll.class);
//                break;
//            case GALLERY:
//                event.card = context.deserialize(jo.get("card"), Gallery.class);
//                break;
//            case UNKNOWN:
//                event.card = null;
//                break;
//        }

        return event;
    }
}
