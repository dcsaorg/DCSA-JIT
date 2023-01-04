package org.dcsa.jit.persistence.entity;

import lombok.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "smdg_delay_reason")
@Getter
public class SMDGDelayReason {

  @Id
  @Column(name="delay_reason_code", length = 5, columnDefinition = "bpchar") // "bpchar" here is not a typing error
  private String delayReasonCode;

  @Column(name="delay_reason_name", length = 100)
  private String delayReasonName;

  @Column(name="delay_reason_description", length = 250)
  private String delayReasonDescription;
}
