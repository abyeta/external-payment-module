package org.jala.university.presentation.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.jala.university.application.dto.ExternalServiceListDto;
import org.jala.university.application.mapper.ExternalServiceListMapper;
import org.jala.university.application.service.ExternalServiceListService;
import org.jala.university.application.service.ExternalServiceListServiceImpl;
import org.jala.university.domain.repository.ExternalServiceListRepository;
import org.jala.university.infrastructure.persistance.ExternalServiceListRepositoryImpl;

import java.util.List;

public final class ExternalServiceListController {

  @FXML private TableView<ExternalServiceListDto> tableExternalServices;
  @FXML private TableColumn<ExternalServiceListDto, String> colName;
  @FXML private TableColumn<ExternalServiceListDto, String> colCode;

  private ExternalServiceListService service;

  @FXML
  public void initialize() {
    colName.setCellValueFactory(new PropertyValueFactory<>("name"));
    colCode.setCellValueFactory(new PropertyValueFactory<>("registrationCode"));

    EntityManagerFactory databaseConnectionFactory = Persistence.createEntityManagerFactory("external-payment-unit");
    EntityManager databaseConnection = databaseConnectionFactory.createEntityManager();

    ExternalServiceListRepository repository = new ExternalServiceListRepositoryImpl(databaseConnection);
    ExternalServiceListMapper mapper = new ExternalServiceListMapper();
    service = new ExternalServiceListServiceImpl(repository, mapper);

    List<ExternalServiceListDto> externalServices = service.findAll();
    tableExternalServices.getItems().setAll(externalServices);

    databaseConnection.close();
    databaseConnectionFactory.close();
  }
}
