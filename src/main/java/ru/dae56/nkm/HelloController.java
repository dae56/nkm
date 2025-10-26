package ru.dae56.nkm;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import ru.dae56.nkm.Node;

public class HelloController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Button ButtonAddNode;

    @FXML
    private Button ButtonRemoveNode;

    @FXML
    private ScrollPane FrontScrollPane;

    @FXML
    private VBox rightPannel;

    @FXML
    private BorderPane MainBorder;

    @FXML
    private TextField TextFieldNodeName;

    @FXML
    private TextField TextFieldWeightLinkage;

    @FXML
    private VBox createPanel;

    ObservableList<String> nameNodes = FXCollections.observableArrayList();

    @FXML
    private ComboBox<String> ComboBoxFrom = new ComboBox<String>(nameNodes);

    @FXML
    private ComboBox<String> ComboBoxTo = new ComboBox<String>(nameNodes);

    @FXML
    private Button RemoveLinkageButton;

    @FXML
    private Button AddLinkageButton;

    @FXML
    void addLinkage(ActionEvent event) {
        String nodeNameFrom = ComboBoxFrom.getSelectionModel().getSelectedItem();
        String nodeNameTo = ComboBoxTo.getSelectionModel().getSelectedItem();


    }

    @FXML
    void removeLinkage(ActionEvent event) {

    }



    @FXML
    void addNode(ActionEvent event) throws Exception {
        if (!TextFieldNodeName.getText().isEmpty() && (TextFieldNodeName.getText().trim()).length() > 0) {
            try {
                Node.addNode(FrontScrollPane, anchorPane, TextFieldNodeName.getText().trim());
                Toast.makeText(createPanel, "Node '" + TextFieldNodeName.getText() + "' created!", "success");
                nameNodes.add(TextFieldNodeName.getText());
            } catch (Exception e) {
                Toast.makeText(createPanel, e.getMessage(), "error");
            }
        }
        TextFieldNodeName.clear();
    }

    @FXML
    void removeNode(ActionEvent event) throws Exception {
        if (!TextFieldNodeName.getText().isEmpty() && (TextFieldNodeName.getText().trim()).length() > 0) {
            try{
                Node.removeNode(anchorPane, TextFieldNodeName.getText());
                Toast.makeText(createPanel, "Node '" + TextFieldNodeName.getText() + "' removed!", "success");
                nameNodes.remove(TextFieldNodeName.getText());
            } catch (Exception e) {
                System.out.println(e);
                if (e.getMessage() != null) {
                    Toast.makeText(createPanel, e.getMessage(), "error");
                }
            }
        }
        TextFieldNodeName.clear();
    }

    @FXML
    void initialize() {
        assert AddLinkageButton != null : "fx:id=\"AddLinkageButton\" was not injected: check your FXML file 'Main-view.fxml'.";
        assert ButtonAddNode != null : "fx:id=\"ButtonAddNode\" was not injected: check your FXML file 'Main-view.fxml'.";
        assert ButtonRemoveNode != null : "fx:id=\"ButtonRemoveNode\" was not injected: check your FXML file 'Main-view.fxml'.";
        assert ComboBoxFrom != null : "fx:id=\"ComboBoxFrom\" was not injected: check your FXML file 'Main-view.fxml'.";
        assert ComboBoxTo != null : "fx:id=\"ComboBoxTo\" was not injected: check your FXML file 'Main-view.fxml'.";
        assert FrontScrollPane != null : "fx:id=\"FrontScrollPane\" was not injected: check your FXML file 'Main-view.fxml'.";
        assert MainBorder != null : "fx:id=\"MainBorder\" was not injected: check your FXML file 'Main-view.fxml'.";
        assert RemoveLinkageButton != null : "fx:id=\"RemoveLinkageButton\" was not injected: check your FXML file 'Main-view.fxml'.";
        assert TextFieldNodeName != null : "fx:id=\"TextFieldNodeName\" was not injected: check your FXML file 'Main-view.fxml'.";
        assert anchorPane != null : "fx:id=\"anchorPane\" was not injected: check your FXML file 'Main-view.fxml'.";
        assert createPanel != null : "fx:id=\"createPanel\" was not injected: check your FXML file 'Main-view.fxml'.";
        assert rightPannel != null : "fx:id=\"rightPannel\" was not injected: check your FXML file 'Main-view.fxml'.";
        assert TextFieldWeightLinkage != null : "fx:id=\"TextFieldWeightLinkage\" was not injected: check your FXML file 'Main-view.fxml'.";

        ComboBoxFrom.setItems(nameNodes);
        ComboBoxTo.setItems(nameNodes);
    }

}
