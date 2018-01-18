package com.nodeal.socket.component;

import com.nodeal.socket.process.ServerProcess;
import org.json.simple.JSONObject;

import java.util.UUID;

public abstract class Command extends ServerProcess<JSONObject> {
    public Command(UUID uuid, JSONObject processBody, Object processingParent) {
        super(uuid, processBody, processingParent);
    }

    public abstract JSONObject perform();
}
