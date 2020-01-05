package Server;

import Network.Request;

import java.io.*;
import java.net.Socket;

public class User implements Runnable {

    private String username;
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

    public String getUsername() {
        return this.username;
    }

    public ObjectOutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public void run() {

        while (socket.isConnected() && !socket.isClosed()) {

            try {
                Request<?> request = (Request<?>) inputStream.readUnshared();
                if (request == null) {
                    continue;
                } else if (request.getType() == Request.RequestType.SEND_MESSAGE) {

                    //send the string to be added to the server
                    server.addMessage(this.username + ": " + (String) request.getData());

                    //update all users
                    for (User user : server.getActiveUsers()) {
                        user.getOutputStream().writeObject(new Request<>(Request.RequestType.CHANGE, server.getLastMessage()));
                    }

                } else if (request.getType() == Request.RequestType.REQUEST_MODEL) {

                    //Set the username of the user
                    this.username = (String) request.getData();

                    //Check if username is available
                    //if its not send back a Request object telling the user to disconnect

                    if (server.addUsername(this.username)) {
                        outputStream.writeObject(new Request<>(Request.RequestType.MODEL, server.getModelFromServer()));

                        //update all users who connected
                        for (User user : server.getActiveUsers()) {

                            //Skip this message if your the one connecting
                            if (user.getUsername().equals(this.username)) {
                                continue;
                            }
                            user.getOutputStream().writeObject(new Request<>(Request.RequestType.CHANGE, this.username + " connected"));
                        }

                    } else {
                        server.removeUser(this);
                        outputStream.writeObject(new Request<>(Request.RequestType.ERROR, null));
                        socket.close();
                    }
                } else if (request.getType() == Request.RequestType.DISCONNECT) {

                    System.out.println(username + " is disconnecting");

                    //update all users who disconnected
                    for (User user : server.getActiveUsers()) {
                        user.getOutputStream().writeObject(new Request<>(Request.RequestType.CHANGE, this.username + " disconnected"));
                    }

                    server.removeUser(this);
                    outputStream.writeObject(new Request<>(Request.RequestType.ERROR, null));
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

        //The connection has been lost remove user from active users and disconnect

    }
}
