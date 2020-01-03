package Network;

import Model.Model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class NetworkClient implements Runnable {

    private Socket socket;
    private Model model;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    public NetworkClient(Socket socket, Model model) {
        this.socket = socket;
        this.model = model;
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendRequest(Request request) {
        try {
            outputStream.writeObject(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {

        while (true) {

            try {
                Request<?> request = (Request<?>) inputStream.readUnshared();
                if (request == null) {
                    continue;
                } else if (request.getType() == Request.RequestType.CHANGE) {

                    System.out.println("Adding message to model");
                    model.addMessage((String) request.getData());

                } else if (request.getType() == Request.RequestType.MODEL) {
                    model.setModel((ArrayList<String>) request.getData());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

    }
}
