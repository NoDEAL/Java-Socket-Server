package com.nodeal.socket.server;

import com.nodeal.socket.component.Result;
import com.nodeal.socket.component.ResultList;
import com.nodeal.socket.constant.ServerState;
import com.nodeal.socket.process.ServerProcess;
import com.nodeal.socket.thread.ServerThread;

import java.util.ArrayList;
import java.util.UUID;

public abstract class Server<ProcessBody, ResultFormat> implements AutoCloseable {
    protected final ArrayList<ServerThread<ProcessBody>> runningThreads;
    protected final ResultList<ResultFormat> resultList;

    protected ServerState serverState;

    public Server() {
        runningThreads = new ArrayList<>();
        resultList = new ResultList<>();

        serverState = ServerState.WAITING;
    }

    public abstract ServerProcess<ProcessBody> makeProcess(Object processingObject, Object... parameters);

    public abstract UUID addProcess(ServerProcess<ProcessBody> process);

    public abstract ResultFormat getResult(UUID uuid);

    @Override
    public void close() {
        while (runningThreads.size() > 0);

        serverState = ServerState.CLOSED;
    }
}
