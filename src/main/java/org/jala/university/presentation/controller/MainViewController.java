package org.jala.university.presentation.controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jala.university.commons.presentation.BaseController;

public class MainViewController extends BaseController {
  @FXML
  private void openExternalServiceList() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ExternalServiceListView.fxml"));
    Parent root = loader.load();

    Stage stage = new Stage();
    stage.setScene(new Scene(root));
    stage.setTitle("External Services List");
    stage.show();
  }

}
