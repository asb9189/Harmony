package Client;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class LoginWindow {

    private boolean finished;
    private Stage mainStage;
    private String username;
    private String ip;
    private String port;

    public void display() {

        mainStage = new Stage();

        this.finished = false;

        //Block user interaction with other windows until this window
        //is closed.
        mainStage.initModality(Modality.APPLICATION_MODAL);

        Label label = new Label("Login");
        VBox vbox = new VBox();

        TextField username = new TextField();
        username.setPromptText("USERNAME");
        username.setFocusTraversable(false);

        TextField ip = new TextField();
        ip.setPromptText("IP");
        ip.setFocusTraversable(false);

        TextField port = new TextField();
        port.setPromptText("PORT");
        port.setFocusTraversable(false);

        HBox buttons = new HBox();

        Button exit = new Button("Exit");

        exit.setOnAction( (e) -> {
            System.exit(0);
        } );

        exit.setFocusTraversable(false);

        Button login = new Button("Login");

        login.setOnAction( (e) -> {

            this.username = username.getText();
            this.ip = ip.getText();
            this.port = port.getText();

            if (this.username != null && this.ip != null && this.port != null &&
                    !this.username.isBlank() && !this.ip.isBlank() && !this.port.isBlank() ) {

                this.finished = true;
                mainStage.close();
            }


        } );

        login.setFocusTraversable(false);

        buttons.getChildren().addAll(exit, login);

        vbox.getChildren().addAll(label, username, ip, port, buttons);

        //Make the scene
        Scene scene = new Scene(vbox);
        mainStage.setScene(scene);
        mainStage.show();

    }

    public boolean getFinished() {
        return this.finished;
    }

    public String getUsername() {
        return this.username;
    }

    public String getIp() {
        return this.ip;
    }

    public String getPort() {
        return this.port;
    }

}
