package org.dcsa.jit.persistence.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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

}
