package org.jala.university.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Value;
import org.jala.university.commons.domain.BaseEntity;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;
import java.util.UUID;

@Entity
@Value
@Builder
public class SampleEntity implements BaseEntity<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column
    String name;

    @CreatedDate
    Date created;

    @LastModifiedDate
    Date updated;

    @Override
    public UUID getId() {
        return id;
    }
}
