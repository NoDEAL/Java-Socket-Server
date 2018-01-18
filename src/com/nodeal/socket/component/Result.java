package com.nodeal.socket.component;

import java.util.UUID;

public abstract class Result<ResultFormat> {
    protected final UUID uuid;
    protected final ResultFormat result;

    public Result(UUID uuid, ResultFormat result) {
        this.uuid = uuid;
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof UUID && o.equals(uuid);
    }

    public UUID getUuid() {
        return uuid;
    }

    public ResultFormat getResult() {
        return result;
    }
}
