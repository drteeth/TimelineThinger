package com.lol.boring;

import org.joda.time.DateTime;

public class Event implements Comparable<Event> {
    public String cardType;
    public DateTime occursAt;
    public boolean read;
    public long id;

    @Override public String toString() {
        return String.format("%s (%d)", cardType, id);
    }

    @Override public int compareTo(Event another) {
        if (occursAt.equals(another.occursAt)) {
            return 0;
        } else if (occursAt.isBefore(another.occursAt)) {
            return 1;
        } else {
            return -1;
        }
    }
}
