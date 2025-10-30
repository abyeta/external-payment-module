package org.jala.university.presentation.store;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jala.university.application.dto.ExternalServiceDto;

import java.util.List;

public final class ExternalServiceDataStore {
  private static final ExternalServiceDataStore INSTANCE = new ExternalServiceDataStore();

  public static ExternalServiceDataStore get() {
    return INSTANCE;
  }

  private final ObservableList<ExternalServiceDto> master =
      FXCollections.observableArrayList();

  private ExternalServiceDataStore() {
  }

  public ObservableList<ExternalServiceDto> masterList() {
    return master;
  }

  public void setAll(final List<ExternalServiceDto> list) {
    master.setAll(list);
  }

  public void add(final ExternalServiceDto dto) {
    master.add(dto);
  }
}
