package org.jala.university.application.dto;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Builder
@Value
public class SampleEntityDto {

    UUID id;
    String name;
}
