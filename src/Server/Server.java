package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

public class Server {

    private int port;
    private HashSet<String> usernames;
    private ServerSocket serverSocket;
    private ArrayList<User> activeUsers;
    private ArrayList<String> messages;


    public Server(int port) {

        this.port = port;
        activeUsers = new ArrayList<User>();
        messages = new ArrayList<>();
        usernames = new HashSet<>();

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port: " + this.port);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public synchronized void addMessage(String msg) {
        messages.add(msg);
    }

    public synchronized String getLastMessage() {
        return messages.get(messages.size() - 1);
    }

    public void startServer() {
        while (true) {
            try {
                assert serverSocket != null;

                //Blocking call
                Socket connectedClient = serverSocket.accept();
                System.out.println(connectedClient.toString());
                User newUser = new User(connectedClient, this);
                activeUsers.add(newUser);
                Thread thread = new Thread(newUser);
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void removeUser(User user) {
        usernames.remove(user.getUsername());
        activeUsers.remove(user);
    }

    public synchronized ArrayList<String> getModelFromServer(){
        return messages;
    }

    public synchronized ArrayList<User> getActiveUsers() {
        return activeUsers;
    }

    public synchronized boolean addUsername(String username) {
        return usernames.add(username);
    }

    public synchronized int getPort() {
        return this.port;
    }

    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Command Usage: Port");
            System.exit(0);
        }
        Server server = new Server(Integer.parseInt(args[0]));
        server.startServer();
    }

}
