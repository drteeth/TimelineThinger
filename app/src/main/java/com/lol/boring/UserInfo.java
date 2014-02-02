package com.lol.boring;

import java.util.Map;

public class UserInfo {
    public Map<Long,Boolean> cardsRead;

    public boolean hasRead(long id) {
        boolean read = false;
        if( cardsRead.containsKey(id) ) {
            read = cardsRead.get(id);
        }
        return read;
    }
}
