package org.jala.university.infrastructure.external.dto.service;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ServiceResponse {
    Long id;
    String name;
    String code;
}
