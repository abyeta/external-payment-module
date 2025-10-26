package org.jala.university.application.service;

import org.jala.university.application.dto.ExternalServiceListDto;
import java.util.List;

public interface ExternalServiceListService {
  List<ExternalServiceListDto> findAll();
}
