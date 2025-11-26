package org.jala.university.application.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO representing a customer entity.
 * Used to transfer customer data between layers.
 */
@Value
@Builder
public class CustomerDto {
    Long id;
    String name;
    List<ExternalServiceDto> linkedServices;
    LocalDateTime createdAt;
}



