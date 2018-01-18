package com.nodeal.socket.process;

import java.util.UUID;

public class ServerProcess<Body> {
    protected final UUID uuid;
    protected final Body processBody;
    protected final Object processingParent;

    public ServerProcess(UUID uuid, Body processBody, Object processingParent) {
        this.uuid = uuid;
        this.processBody = processBody;
        this.processingParent = processingParent;
    }

    public void notifyToParent() {
        synchronized (processingParent) {
            processingParent.notify();
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public Body getProcessBody() {
        return processBody;
    }

    public Object getProcessingParent() {
        return processingParent;
    }
}
