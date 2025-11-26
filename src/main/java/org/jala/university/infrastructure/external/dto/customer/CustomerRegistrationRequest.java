package org.jala.university.infrastructure.external.dto.customer;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CustomerRegistrationRequest {
    String identification;
    String name;
    String email;
}
