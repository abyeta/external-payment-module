package org.jala.university.presentation.controller;

import com.mysql.cj.xdevapi.JsonArray;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import org.jala.university.commons.presentation.BaseController;

public class ExternalServiceController extends BaseController {
    public Label feedbackLabel;
    public Button searchButton;
    public Button backButton;
    public Label serviceNameLabel;



    public void onBackToMain(ActionEvent actionEvent) {

    }

    public void onSearch(ActionEvent actionEvent) {
        if(serviceNameLabel.getText() == null || serviceNameLabel.getText().equals("")) {
            showFeedback();
        } else {

        }
    }

    public void showFeedback() {
        feedbackLabel.setText("The code user is required");
        feedbackLabel.setVisible(true);
    }
}
