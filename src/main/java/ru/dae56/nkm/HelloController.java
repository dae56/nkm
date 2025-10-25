package ru.dae56.nkm;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class HelloController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane AnchorPane;

    @FXML
    private Button ButtonAddNode;

    @FXML
    private Button ButtonRemoveNode;

    @FXML
    private ScrollPane FrontScrollPane;

    @FXML
    private BorderPane MainBorder;

    @FXML
    private TextField TextFieldNodeName;

    @FXML
    void addNode(ActionEvent event) {

    }

    @FXML
    void removeNode(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert AnchorPane != null : "fx:id=\"AnchorPane\" was not injected: check your FXML file 'Main-view.fxml'.";
        assert ButtonAddNode != null : "fx:id=\"ButtonAddNode\" was not injected: check your FXML file 'Main-view.fxml'.";
        assert ButtonRemoveNode != null : "fx:id=\"ButtonRemoveNode\" was not injected: check your FXML file 'Main-view.fxml'.";
        assert FrontScrollPane != null : "fx:id=\"FrontScrollPane\" was not injected: check your FXML file 'Main-view.fxml'.";
        assert MainBorder != null : "fx:id=\"MainBorder\" was not injected: check your FXML file 'Main-view.fxml'.";
        assert TextFieldNodeName != null : "fx:id=\"TextFieldNodeName\" was not injected: check your FXML file 'Main-view.fxml'.";

    }

}
