package com.nodeal.socket.component.command;

import com.nodeal.socket.component.Command;
import com.nodeal.socket.util.JSONUtil;
import org.json.simple.JSONObject;

import java.util.UUID;

public class Echo extends Command {
    public static final String NAME = "ECHO";

    public Echo(UUID uuid, JSONObject processBody, Object processingParent) {
        super(uuid, processBody, processingParent);
    }

    @Override
    public JSONObject perform() {
        String sender = (String) processBody.get("from");
        String message = (String) processBody.get("message");

        System.out.printf("echo from %s, message: %s\n", sender, message);

        return JSONUtil.makeOKMessage();
    }
}
