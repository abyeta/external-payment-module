package org.jala.university.application.mapper;

import org.jala.university.application.dto.ExternalServiceDto;
import org.jala.university.application.dto.ExternalServiceRegistrationRequestDto;
import org.jala.university.commons.application.mapper.Mapper;
import org.jala.university.domain.entity.ExternalService;
import org.jala.university.domain.entity.Holder;

import java.time.LocalDateTime;

/**
 * Mapper for converting between ExternalService entity and ExternalServiceDto.
 * Also provides mapping from registration request DTOs to entities.
 */
public final class ExternalServiceMapper implements Mapper<ExternalService, ExternalServiceDto> {

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
                .contractExpiration(externalService.getContractExpiration())
                .contactDetails(externalService.getContactDetails())
                .accountNumber(externalService.getAccountNumber())
                .enabled(externalService.isEnabled())
                .createdAt(externalService.getCreatedAt())
                .updatedAt(externalService.getUpdatedAt())
                .build();
    }

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
                .accountNumber(externalServiceDto.getAccountNumber())
                .enabled(externalServiceDto.isEnabled())
                .createdAt(externalServiceDto.getCreatedAt())
                .updatedAt(externalServiceDto.getUpdatedAt())
                .build();
    }

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
                .contractExpiration(requestDto.getContractExpiration())
                .contactDetails(requestDto.getContactDetails())
                .holder(Holder.builder()
                        .name(requestDto.getHolder().getName())
                        .identificationNumber(requestDto.getHolder().getIdentificationNumber())
                        .email(requestDto.getHolder().getEmail())
                        .landlinePhone(requestDto.getHolder().getLandlinePhone())
                        .build())
                .createdAt(LocalDateTime.now())
                .enabled(true)
                .build();
    }

    public void updateFromRequest(ExternalServiceRegistrationRequestDto request, ExternalService entity) {
        if (request == null || entity == null) {
            return;
        }

        if (request.getProviderName() != null && !request.getProviderName().isBlank()) {
            entity.setProviderName(request.getProviderName());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            entity.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            entity.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getPhoneCountryCode() != null && !request.getPhoneCountryCode().isBlank()) {
            entity.setPhoneCountryCode(request.getPhoneCountryCode());
        }
        if (request.getAccountReference() != null && !request.getAccountReference().isBlank()) {
            entity.setAccountReference(request.getAccountReference());
        }

        entity.setUpdatedAt(LocalDateTime.now());
    }
}


