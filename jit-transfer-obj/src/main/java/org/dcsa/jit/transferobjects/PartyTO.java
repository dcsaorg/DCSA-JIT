package org.dcsa.jit.transferobjects;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

/**
 * NOTE: This Party is specific to JIT and should not be added to Shared-Kernel.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "party")
public class PartyTO {
  @Id
  @GeneratedValue
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "party_name", length = 100)
  private String name;

  @Column(name = "tax_reference_1", length = 20)
  private String taxReference1;

  @Column(name = "tax_reference_2", length = 20)
  private String taxReference2;

  @Column(name = "public_key", length = 100)
  private String publicKey;

  // TODO fix this reference
  // @ManyToOne(fetch = FetchType.LAZY)
  // @JoinColumn(name = "address_id")
  // private Address address;
}
