package org.dcsa.jit.persistence.entity;

import lombok.*;
import org.dcsa.jit.persistence.entity.enums.PublisherRole;

import jakarta.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "publisher_pattern")
public class PublisherPattern {

  @Id
  @Column(name = "pattern_id", nullable = false)
  private String id;

  @Enumerated(EnumType.STRING)
  @Column(name = "publisher_role", length = 3, nullable = false)
  private PublisherRole publisherRole;

  @Enumerated(EnumType.STRING)
  @Column(name = "primary_receiver", length = 3, nullable = false)
  private PublisherRole primaryReceiver;
}
