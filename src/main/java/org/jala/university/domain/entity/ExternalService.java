package org.jala.university.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.jala.university.commons.domain.entity.BaseEntity;
import java.util.UUID;

@Entity
@Data
@Builder
public final class ExternalService implements BaseEntity<UUID> {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  UUID id;

  @Column
  String name;

  @Column
  String registrationCode;

  @Override
  public UUID getId() {
    return id;
  }
}
