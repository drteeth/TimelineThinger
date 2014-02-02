package com.lol.boring;

import java.util.List;

public class Timeline {
    public final List<Event> pastEvents;
    public final List<Event> newEvents;

    public Timeline(List<Event> pastEvents, List<Event> newEvents) {
        this.pastEvents = pastEvents;
        this.newEvents = newEvents;
    }
}
