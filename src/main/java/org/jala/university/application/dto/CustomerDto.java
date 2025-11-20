package org.jala.university.application.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO representing a customer entity.
 * Used to transfer customer data between layers.
 */
@Value
@Builder
public class CustomerDto {
    UUID id;
    String name;
    List<ExternalServiceDto> linkedServices;
    LocalDateTime createdAt;
}


