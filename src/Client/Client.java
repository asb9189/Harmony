package Client;
import Model.Model;
import Network.NetworkClient;
import Network.Request;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client extends Application {

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
        List< String > args = getParameters().getRaw();
        ip = args.get(0);
        port = Integer.parseInt(args.get(1));

        System.out.println("Attempting to connect");
        try {
            socket = new Socket(ip, port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Connected");

        model = new Model();
        model.addObserver(this);
        networkClient = new NetworkClient(socket, model);
        Thread thread = new Thread(networkClient);
        thread.start();

        textArea = new TextArea();
        textArea.setDisable(true);

        //Request an updated model
        networkClient.sendRequest(new Request(Request.RequestType.REQUEST_MODEL, null));

        while (!model.isModelSet()) {
            System.out.println("model not set");
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
       }));
       borderPane.setRight(send);

       scene = new Scene(borderPane);
       stage.setScene(scene);

       stage.show();
    }

    public void stop() {

    }

    public static void main(String[] args) {

        // Args: Hostname Port

        if (args.length != 2) {
            System.out.println("Command Usage: IP Port");
            System.exit(0);
        }

        Application.launch(args);
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
}
