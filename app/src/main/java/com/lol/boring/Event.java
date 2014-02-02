package com.lol.boring;

import org.joda.time.DateTime;

public class Event {
    public String cardType;
    public DateTime occursAt;
    public boolean read;
    public long id;

    @Override public String toString() {
        return String.format("%s (%d)", cardType, id);
    }
}
