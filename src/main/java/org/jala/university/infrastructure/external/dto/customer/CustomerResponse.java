package org.jala.university.infrastructure.external.dto.customer;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CustomerResponse {
    Long id;
    String identification;
    String name;
    String email;
}
