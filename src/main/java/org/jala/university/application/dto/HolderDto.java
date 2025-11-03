package org.jala.university.application.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Value
public class HolderDto {

    UUID id;
    String name;
    String identificationNumber;
    String email;
    String landlinePhone;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
