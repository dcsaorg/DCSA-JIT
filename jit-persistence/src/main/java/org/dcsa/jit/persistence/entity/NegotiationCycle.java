package org.dcsa.jit.persistence.entity;

import lombok.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "negotiation_cycle")
public class NegotiationCycle {

  @Id
  @Column(name = "cycle_key", nullable = false)
  private String cycleKey;


  @Column(name = "cycle_name", nullable = false)
  private String cycleName;

  @Column(name = "display_order", nullable = false)
  private int displayOrder;
}
