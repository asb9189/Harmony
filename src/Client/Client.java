package Client;
import Model.Model;
import Network.NetworkClient;
import Network.Request;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Client extends Application {

    private String username;
    private String ip;
    private int port;
    private Socket socket;
    private NetworkClient networkClient;
    private Model model;

    /** GUI References */
    private BorderPane borderPane;
    private TextArea textArea;
    private Scene scene;
    private TextField textField;
    private Button send;

    public void init() {

        LoginWindow login = new LoginWindow();
        Platform.runLater(login::display);

        while (!login.getFinished()) {
            Thread.yield();
        }

        username = login.getUsername().strip();
        ip = login.getIp().strip();
        port = Integer.parseInt(login.getPort());

        System.out.println(username);
        System.out.println(ip);
        System.out.println(port);

        try {
            socket = new Socket(ip, port);
        } catch (IOException e) {
            System.out.println("Failed to connect");
            System.exit(0);
        }

        model = new Model();
        model.addObserver(this);
        networkClient = new NetworkClient(socket, model);
        Thread thread = new Thread(networkClient);
        thread.start();

        textArea = new TextArea();
        textArea.setDisable(true);
        textArea.setStyle("-fx-opacity: 1");

        //Request an updated model
        networkClient.sendRequest(new Request(Request.RequestType.REQUEST_MODEL, username));

        while (!model.isModelSet()) {
            Thread.yield();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {

       borderPane = new BorderPane();
       textField = new TextField();
       borderPane.setCenter(textArea);
       borderPane.setBottom(textField);
       send = new Button("Send");
       send.setOnAction( (actionEvent -> {
           Request<String> request = new Request<>(Request.RequestType.SEND_MESSAGE, textField.getText());
           networkClient.sendRequest(request);
           textField.setText("");
       }));
       borderPane.setRight(send);

       scene = new Scene(borderPane);
       stage.setScene(scene);

       stage.show();
    }

    public void stop() {
        networkClient.sendRequest(new Request(Request.RequestType.DISCONNECT, null));
    }

    public void update(Model model, ArrayList<String> messages) {

        this.model = model;
        textArea.appendText("\n" + messages.get(messages.size() - 1));

    }

    public void updateFresh(Model model, ArrayList<String> messages) {
        this.model = model;
        for (String s : messages) {
            textArea.appendText("\n" + s);
        }
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
