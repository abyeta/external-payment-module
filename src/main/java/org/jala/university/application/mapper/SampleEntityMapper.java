package org.jala.university.application.mapper;

import org.jala.university.application.dto.SampleEntityDto;
import org.jala.university.commons.application.mapper.Mapper;
import org.jala.university.domain.entity.SampleEntity;

public class SampleEntityMapper implements Mapper<SampleEntity, SampleEntityDto> {

    @Override
    public SampleEntityDto mapTo(SampleEntity sampleEntity) {
        return SampleEntityDto.builder()
                .id(sampleEntity.getId())
                .name(sampleEntity.getName())
                .build();
    }

    @Override
    public SampleEntity mapFrom(SampleEntityDto sampleEntityDto) {
        return SampleEntity.builder()
                .id(sampleEntityDto.getId())
                .name(sampleEntityDto.getName())
                .build();
    }
}
