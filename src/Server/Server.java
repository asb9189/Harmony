package Server;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private int port;
    private ServerSocket serverSocket;
    private ArrayList<User> activeUsers;
    private ArrayList<String> messages;


    public Server(int port) {

        this.port = port;
        activeUsers = new ArrayList<User>();
        messages = new ArrayList<>();

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void addMessage(String msg) {
        messages.add(msg);
    }

    public String getLastMessage() {
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

    public boolean removeUser(User user) {
        return activeUsers.remove(user);
    }

    public ArrayList<String> getModelFromServer(){
        return messages;
    }

    public ArrayList<User> getActiveUsers() {
        return activeUsers;
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
