package com.nodeal.socket.server;

import com.nodeal.socket.constant.Response;
import com.nodeal.socket.constant.ServerState;
import com.nodeal.socket.process.ServerProcess;
import com.nodeal.socket.thread.ServerThread;
import com.nodeal.socket.util.JSONUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

public class SocketServer extends Server<Socket, Void> {
    public static final int SERVER_PORT = 8080;

    private static SocketServer instance;

    static {
        try {
            instance = new SocketServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SocketServer getInstance() {
        return instance;
    }

    private ServerSocket serverSocket;

    private SocketServer() throws IOException {
        serverSocket = new ServerSocket(SERVER_PORT);

        serverState = ServerState.RUNNING;

        while (serverState == ServerState.RUNNING) {
            addProcess(makeProcess(null, serverSocket.accept()));
        }
    }

    @Override
    public ServerProcess<Socket> makeProcess(Object processingObject, Object... parameters) {
        assert parameters.length == 1 && parameters[0] instanceof Socket;

        return new ServerProcess<>(null, (Socket) parameters[0], processingObject);
    }

    @Override
    public UUID addProcess(ServerProcess<Socket> process) {
        try {
            SocketThread socketThread = new SocketThread(process);

            runningThreads.add(socketThread);
            socketThread.start();

            return process.getUuid();
        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }
    }

    @Override
    public Void getResult(UUID uuid) {
        return null;
    }

    public static class SocketThread extends ServerThread<Socket> {
        private final Socket socket;
        private final DataInputStream dataInputStream;
        private final DataOutputStream dataOutputStream;

        public SocketThread(ServerProcess<Socket> serverProcess) throws IOException {
            super(serverProcess);

            this.socket = serverProcess.getProcessBody();
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        }

        @Override
        public void run() {
            try {
                try {
                    String rawMessage = dataInputStream.readUTF();

                    while (!isInterrupted()) {
                        System.out.printf("from: %s, json: %s\n", socket.getInetAddress(), rawMessage);

                        JSONObject clientMessage = (JSONObject) JSONUtil.getJsonParser().parse(rawMessage);

                        String commandName = (String) clientMessage.get("command");
                        JSONObject arguments = (JSONObject) clientMessage.get("arguments");
                        arguments.put("from", clientMessage.get("from"));

                        if (commandName.equals("CLOSE")) {
                            dataOutputStream.writeUTF(JSONUtil.makeOKMessage().toJSONString());
                            dataOutputStream.flush();

                            break;
                        }

                        CommandServer commandServer = CommandServer.getInstance();
                        UUID uuid = commandServer.addProcess(
                                commandServer.makeProcess(this, commandName, arguments)
                        );

                        synchronized (this) {
                            wait();
                        }

                        JSONObject commandResult = commandServer.getResult(uuid);

                        dataOutputStream.writeUTF(commandResult.toJSONString());
                        dataOutputStream.flush();

                        if (dataInputStream.available() > 0)
                            rawMessage = dataInputStream.readUTF();
                        else
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    dataOutputStream.writeUTF(JSONUtil.makeErrorMessage(Response.UNKNOWN_IO_ERROR).toJSONString());
                    dataOutputStream.flush();
                } catch (ParseException e) {
                    e.printStackTrace();

                    dataOutputStream.writeUTF(JSONUtil.makeErrorMessage(Response.INVALID_JSON).toJSONString());
                    dataOutputStream.flush();
                } catch (InterruptedException e) {
                    e.printStackTrace();

                    dataOutputStream.writeUTF(JSONUtil.makeErrorMessage(Response.THREAD_INTERRUPTED_WHILE_WAITING).toJSONString());
                    dataOutputStream.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                dataInputStream.close();
                dataOutputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(getInstance().runningThreads);
            System.out.println(this);
            instance.runningThreads.remove(this);
        }
    }
}
