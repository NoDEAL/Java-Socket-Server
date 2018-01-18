package com.nodeal.socket.server;

import com.nodeal.socket.component.Command;
import com.nodeal.socket.component.Result;
import com.nodeal.socket.component.command.Echo;
import com.nodeal.socket.process.ServerProcess;
import com.nodeal.socket.thread.ServerThread;
import org.json.simple.JSONObject;

import java.util.UUID;

public class CommandServer extends Server<JSONObject, JSONObject> {
    private static CommandServer instance;

    public static CommandServer getInstance() {
        if (instance == null) instance = new CommandServer();

        return instance;
    }

    @Override
    public ServerProcess<JSONObject> makeProcess(Object processingObject, Object... parameters) {
        assert parameters.length == 2 &&
                parameters[0] instanceof String &&
                parameters[1] instanceof JSONObject;

        String commandName = (String) parameters[0];
        JSONObject arguments = (JSONObject) parameters[1];
        UUID uuid = UUID.randomUUID();

        switch (commandName) {
            case Echo.NAME: return new Echo(uuid, arguments, processingObject);
        }

        return null;
    }

    @Override
    public UUID addProcess(ServerProcess<JSONObject> process) {
        CommandThread commandThread = new CommandThread((Command) process);
        runningThreads.add(commandThread);
        commandThread.start();

        return process.getUuid();
    }

    @Override
    public JSONObject getResult(UUID uuid) {
        return resultList.remove(uuid).getResult();
    }

    public static class CommandThread extends ServerThread<JSONObject> {
        public CommandThread(Command command) {
            super(command);
        }

        @Override
        public void run() {
            instance.resultList.add(
                    new Result<JSONObject>(process.getUuid(), ((Command) process).perform()) {}
            );

            process.notifyToParent();
            instance.runningThreads.remove(this);
        }
    }
}
