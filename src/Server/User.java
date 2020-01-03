package Server;

import Network.Request;

import java.io.*;
import java.net.Socket;

public class User implements Runnable {

    private Socket socket;
    private Server server;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    public User(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Failed to initialize input/output streams");
        }
    }

    public ObjectOutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public void run() {

        while (true) {

            try {
                Request<?> request = (Request<?>) inputStream.readUnshared();
                if (request == null) {
                    continue;
                } else if (request.getType() == Request.RequestType.SEND_MESSAGE) {

                    //send the string to be added to the server
                    server.addMessage((String) request.getData());

                    //update all users
                    for (User user : server.getActiveUsers()) {
                        user.getOutputStream().writeObject(new Request<>(Request.RequestType.CHANGE, server.getLastMessage()));
                    }

                } else if (request.getType() == Request.RequestType.REQUEST_MODEL) {
                    outputStream.writeObject(new Request<>(Request.RequestType.MODEL, server.getModelFromServer()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

    }
}
