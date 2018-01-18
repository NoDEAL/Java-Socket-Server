package com.nodeal.socket.thread;

import com.nodeal.socket.process.ServerProcess;

public abstract class ServerThread<ProcessBody> extends Thread {
    protected final ServerProcess<ProcessBody> process;

    public ServerThread(ServerProcess<ProcessBody> process) {
        this.process = process;
    }

    @Override
    public abstract void run();
}
