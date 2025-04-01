package org.jala.university.application.service;

import org.jala.university.application.dto.SampleEntityDto;
import org.jala.university.application.mapper.SampleEntityMapper;
import org.jala.university.domain.entity.SampleEntity;
import org.jala.university.domain.repository.SampleEntityRepository;

public final class ExternalPaymentServiceImpl implements ExternalPaymentService {
    private final SampleEntityRepository sampleEntityRepository;
    private final SampleEntityMapper sampleEntityMapper;

    public ExternalPaymentServiceImpl(SampleEntityRepository sampleEntityRepository,
                                      SampleEntityMapper sampleEntityMapper) {
        this.sampleEntityRepository = sampleEntityRepository;
        this.sampleEntityMapper = sampleEntityMapper;
    }
    // Here should be added all the functionality to handle the business logic
    @Override
    public SampleEntityDto doSomething(SampleEntityDto sampleEntityDto) {
        SampleEntity saved = sampleEntityRepository.save(sampleEntityMapper.mapFrom(sampleEntityDto));
        return sampleEntityMapper.mapTo(saved);
    }
}
