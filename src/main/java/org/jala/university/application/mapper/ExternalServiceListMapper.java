package org.jala.university.application.mapper;

import org.jala.university.application.dto.ExternalServiceListDto;
import org.jala.university.commons.application.mapper.Mapper;
import org.jala.university.domain.entity.ExternalService;

public final class ExternalServiceListMapper implements Mapper<ExternalService, ExternalServiceListDto> {

  @Override
  public ExternalServiceListDto mapTo(ExternalService entity) {
    return ExternalServiceListDto.builder()
        .id(entity.getId())
        .name(entity.getName())
        .registrationCode(entity.getRegistrationCode())
        .build();
  }

  @Override
  public ExternalService mapFrom(ExternalServiceListDto dto) {
    return ExternalService.builder()
        .id(dto.getId())
        .name(dto.getName())
        .registrationCode(dto.getRegistrationCode())
        .build();
  }
}
