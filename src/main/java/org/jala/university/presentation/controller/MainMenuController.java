package org.jala.university.presentation.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.jala.university.commons.presentation.BaseController;

public class MainMenuController extends BaseController {

    @FXML
    private GridPane root;
    @FXML
    private VBox rightMenu;
    @FXML
    private ScrollPane enabledServicesScroll;
    @FXML
    private ScrollPane disabledServicesScroll;
    @FXML
    private VBox enabledHBox;
    @FXML
    private VBox disabledHBox;
    @FXML
    private Button editButton;
    @FXML
    private Button disableButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button addButton;

    @FXML
    private Label message;

    private String service;
    @FXML
    public final void initialize() {
        insertFakeValues(enabledHBox);
        insertFakeValues(disabledHBox);

    }

    public final void insertFakeValues(VBox vBox) {
        HBox firstLine = new HBox();
        HBox secondLine = new HBox();

        final int spacing = 10;

        firstLine.setSpacing(spacing);
        secondLine.setSpacing(spacing);

        final int quantity = 16;
        for (int i = 0; i < quantity; i++) {
            VBox objectBox = getServiceBox();

            String serviceName = "service" + (i + 1);
            Label nameLabel = new Label(serviceName);
            HBox serviceNameBox = new HBox(nameLabel);
            nameLabel.setStyle("-fx-font-size: 25");
            serviceNameBox.setAlignment(Pos.TOP_LEFT);

            Label codelabel = new Label("code" + (i + 1));
            HBox codeBox = new HBox(codelabel);
            codeBox.setAlignment(Pos.BOTTOM_RIGHT);
            objectBox.getChildren().addAll(serviceNameBox, codeBox);

            VBox.setVgrow(serviceNameBox, Priority.ALWAYS);
            if (i % 2 == 0) {
                firstLine.getChildren().add(objectBox);
            } else {
                secondLine.getChildren().add(objectBox);
            }
            objectBox.setOnMouseClicked(event -> service = serviceName);
        }

        VBox.setVgrow(firstLine, Priority.SOMETIMES);
        VBox.setVgrow(secondLine, Priority.SOMETIMES);

        vBox.getChildren().addAll(firstLine, secondLine);
    }

    public final VBox getServiceBox() {
        VBox objectBox = new VBox();
        objectBox.setStyle("-fx-background-color: rgba(0,98,255,0.44)");

        final int width = 150;
        final int heigh = 100;
        objectBox.setPrefWidth(width);
        objectBox.setMinWidth(width);
        objectBox.setPrefHeight(heigh);
        objectBox.setMinHeight(heigh);
        return objectBox;
    }

    public final void edit(ActionEvent event) {
        if (service == null) {
            message.setText("selecciona un servicio");
            message.setVisible(true);
            return;
        }
        message.setText("editando " + service);
        message.setVisible(true);
    }

    public final void disable(ActionEvent event) {
        if (service == null) {
            message.setText("selecciona un servicio");
            message.setVisible(true);
            return;
        }
        message.setText("deshabilitando " + service);
        message.setVisible(true);
    }

    public final void delete(ActionEvent event) {
        if (service == null) {
            message.setText("selecciona un servicio");
            message.setVisible(true);
            return;
        }
        message.setText("borrando " + service);
        message.setVisible(true);
    }

    public final void addService(ActionEvent event) {

        message.setText("agregando servicio ");
        message.setVisible(true);
    }

    public final void enterServices(ActionEvent event) {

    }
}
