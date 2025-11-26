package org.jala.university.infrastructure.external.dto.service;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ServiceRegistrationRequest {
    String name;
    String code;
}
