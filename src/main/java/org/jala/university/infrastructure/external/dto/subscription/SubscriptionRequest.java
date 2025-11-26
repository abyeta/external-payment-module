package org.jala.university.infrastructure.external.dto.subscription;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SubscriptionRequest {
    String customerIdentification;
    String serviceCode;
}
