package org.jala.university.application.service;

import org.jala.university.application.dto.ExternalServiceListDto;
import org.jala.university.application.mapper.ExternalServiceListMapper;
import org.jala.university.domain.repository.ExternalServiceListRepository;
import java.util.List;
import java.util.stream.Collectors;

public final class ExternalServiceListServiceImpl implements ExternalServiceListService {

  private final ExternalServiceListRepository repository;
  private final ExternalServiceListMapper mapper;

  public ExternalServiceListServiceImpl(ExternalServiceListRepository repository, ExternalServiceListMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public List<ExternalServiceListDto> findAll() {
    var services = repository.findAll();
    return services.stream()
        .map(mapper::mapTo)
        .collect(Collectors.toList());
  }
}
