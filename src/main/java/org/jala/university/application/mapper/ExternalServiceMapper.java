package org.jala.university.application.mapper;

import org.jala.university.application.dto.ExternalServiceDto;
import org.jala.university.application.dto.ExternalServiceRegistrationRequestDto;
import org.jala.university.commons.application.mapper.Mapper;
import org.jala.university.domain.entity.ExternalService;

import java.time.LocalDateTime;

/**
 * Mapper for converting between ExternalService entity and ExternalServiceDto.
 * Also provides mapping from registration request DTOs to entities.
 */
public final class ExternalServiceMapper implements Mapper<ExternalService, ExternalServiceDto> {

    /**
     * Maps an ExternalService entity to an ExternalServiceDto.
     *
     * @param externalService the entity to map
     * @return the mapped DTO
     */
    @Override
    public ExternalServiceDto mapTo(ExternalService externalService) {
        if (externalService == null) {
            return null;
        }

        return ExternalServiceDto.builder()
                .id(externalService.getId())
                .providerName(externalService.getProviderName())
                .accountReference(externalService.getAccountReference())
                .phoneCountryCode(externalService.getPhoneCountryCode())
                .phoneNumber(externalService.getPhoneNumber())
                .email(externalService.getEmail())
                .contactDetails(externalService.getContactDetails())
                .createdAt(externalService.getCreatedAt())
                .updatedAt(externalService.getUpdatedAt())
                .build();
    }

    /**
     * Maps an ExternalServiceDto to an ExternalService entity.
     *
     * @param externalServiceDto the DTO to map
     * @return the mapped entity
     */
    @Override
    public ExternalService mapFrom(ExternalServiceDto externalServiceDto) {
        if (externalServiceDto == null) {
            return null;
        }

        return ExternalService.builder()
                .id(externalServiceDto.getId())
                .providerName(externalServiceDto.getProviderName())
                .accountReference(externalServiceDto.getAccountReference())
                .phoneCountryCode(externalServiceDto.getPhoneCountryCode())
                .phoneNumber(externalServiceDto.getPhoneNumber())
                .email(externalServiceDto.getEmail())
                .contactDetails(externalServiceDto.getContactDetails())
                .createdAt(externalServiceDto.getCreatedAt())
                .updatedAt(externalServiceDto.getUpdatedAt())
                .build();
    }

    /**
     * Maps a registration request DTO to an ExternalService entity with DRAFT status.
     * Sets the current timestamp as creation time.
     *
     * @param requestDto the registration request DTO
     * @return the mapped entity with DRAFT status
     */
    public ExternalService mapFromRequest(ExternalServiceRegistrationRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }

        return ExternalService.builder()
                .providerName(requestDto.getProviderName())
                .accountReference(requestDto.getAccountReference())
                .phoneCountryCode(requestDto.getPhoneCountryCode())
                .phoneNumber(requestDto.getPhoneNumber())
                .email(requestDto.getEmail())
                .contactDetails(requestDto.getContactDetails())
                .createdAt(LocalDateTime.now())
                .build();
    }

}

