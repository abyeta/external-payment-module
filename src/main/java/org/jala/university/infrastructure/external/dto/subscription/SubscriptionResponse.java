package org.jala.university.infrastructure.external.dto.subscription;

import lombok.Builder;
import lombok.Value;
import org.jala.university.infrastructure.external.dto.customer.CustomerResponse;
import org.jala.university.infrastructure.external.dto.service.ServiceResponse;

@Value
@Builder
public class SubscriptionResponse {
    Long id;
    CustomerResponse customer;
    ServiceResponse service;
    String paymentCode;
    Boolean active;
}
