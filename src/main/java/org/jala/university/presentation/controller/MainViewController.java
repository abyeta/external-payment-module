//package org.jala.university.presentation.controller;
//
//import javafx.fxml.FXML;
//import org.jala.university.commons.presentation.BaseController;
//import org.jala.university.commons.presentation.ViewSwitcher;
//import org.jala.university.presentation.ExternalPaymentView;
//
///**
// * Controller for the main view of the External Payment Module.
// * Provides navigation to different module features.
// */
//public class MainViewController extends BaseController {
//
//    /**
//     * Navigates to the External Service Registration view.
//     * This method is called when the user clicks the "Register Service" button.
//     */
//    @FXML
//    private void onRegisterService() {
//        ViewSwitcher.switchTo(ExternalPaymentView.EXTERNAL_SERVICE_REGISTRATION.getView());
//    }
//
//    /**
//     * Navigates back to the main view.
//     * This method can be used by other views to return to the main menu.
//     */
//    @FXML
//    private void onBackToMain() {
//        ViewSwitcher.switchTo(ExternalPaymentView.MAIN.getView());
//    }
//}
