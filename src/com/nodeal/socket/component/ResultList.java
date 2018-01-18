package com.nodeal.socket.component;

import java.util.ArrayList;
import java.util.UUID;

public class ResultList<ResultFormat> extends ArrayList<Result<ResultFormat>> {
    public int indexOf(UUID uuid) {
        for (int i = 0; i < size(); i++)
            if (get(i).equals(uuid)) return i;

        return -1;
    }

    public boolean contains(UUID uuid) {
        return indexOf(uuid) != -1;
    }

    public Result<ResultFormat> get(UUID uuid) {
        return super.get(indexOf(uuid));
    }

    public Result<ResultFormat> remove(UUID uuid) {
        return super.remove(indexOf(uuid));
    }
}
