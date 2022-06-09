package org.dcsa.jit.persistence.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dcsa.skernel.domain.persistence.entity.Address;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "party")
public class Party {
  @Id
  @GeneratedValue
  @Column(name = "id", length = 100, nullable = false)
  private String id;

  @Column(name = "party_name", length = 100)
  private String name;

  @Column(name = "tax_reference_1", length = 20)
  private String taxReference1;

  @Column(name = "tax_reference_2", length = 20)
  private String taxReference2;

  @Column(name = "public_key", length = 100)
  private String publicKey;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "address_id")
  private Address address;
}
