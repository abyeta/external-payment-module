package org.jala.university.presentation.controller;

import javafx.fxml.FXML;
import org.jala.university.commons.presentation.BaseController;
import org.jala.university.commons.presentation.ViewSwitcher;
import org.jala.university.presentation.ExternalPaymentView;

public final class StartMenuController extends BaseController {

  @FXML
  private void goAdmin() {
    ViewSwitcher.switchTo(ExternalPaymentView.MAIN.getView());
  }

  @FXML
  private void goUser() {
    ViewSwitcher.switchTo(ExternalPaymentView.USER_HOME.getView());
  }
}
