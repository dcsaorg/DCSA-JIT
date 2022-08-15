package org.dcsa.jit.persistence.entity;

import lombok.*;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@NamedEntityGraph(
  name = "graph.allAttributes",
  attributeNodes = {
    @NamedAttributeNode(value = "operationsEvent", subgraph = "subgraph.operationsEvent"),
    @NamedAttributeNode(value = "timestampDefinition", subgraph = "subgraph.timestampDefinition"),
    @NamedAttributeNode("unmappedEvent"),
    @NamedAttributeNode("pendingEvents"),
  },
  subgraphs = {
    @NamedSubgraph(name = "subgraph.operationsEvent", attributeNodes = {
      @NamedAttributeNode(value = "publisher", subgraph = "subgraph.party"),
      @NamedAttributeNode(value = "eventLocation", subgraph = "subgraph.location"),
      @NamedAttributeNode(value = "vesselPosition", subgraph = "subgraph.location"),
      @NamedAttributeNode(value = "transportCall", subgraph = "subgraph.transportCall"),
    }),
    @NamedSubgraph(name = "subgraph.party", attributeNodes = {
      @NamedAttributeNode("address")
    }),
    @NamedSubgraph(name = "subgraph.facility", attributeNodes = {
      // No subgraph to location (it trips a stack overflow) and we do not use a location inside facility
      // (We still expand location itself to ensure JPA notice it is null)
      @NamedAttributeNode(value = "location"),
    }),
    @NamedSubgraph(name = "subgraph.location", attributeNodes = {
      @NamedAttributeNode(value = "address"),
      @NamedAttributeNode(value = "facility",  subgraph = "subgraph.facility" ),
    }),
    @NamedSubgraph(name = "subgraph.vessel", attributeNodes = {
      @NamedAttributeNode(value = "vesselOperatorCarrier"),
    }),
    @NamedSubgraph(name = "subgraph.voyage", attributeNodes = {
      @NamedAttributeNode(value = "service"),
    }),
    @NamedSubgraph(name = "subgraph.transportCall", attributeNodes = {
      @NamedAttributeNode(value = "exportVoyage", subgraph = "subgraph.voyage"),
      @NamedAttributeNode(value = "importVoyage", subgraph = "subgraph.voyage"),
      @NamedAttributeNode(value = "vessel", subgraph = "subgraph.vessel"),
      @NamedAttributeNode(value = "location", subgraph = "subgraph.location"),
      @NamedAttributeNode(value = "facility", subgraph = "subgraph.facility"),
    }),
    @NamedSubgraph(name = "subgraph.timestampDefinition", attributeNodes = {
      @NamedAttributeNode(value = "publisherPattern")
    }),
  })
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "ops_event_timestamp_definition")
public class TimestampInfo implements Persistable<UUID> {

  @Id
  private UUID eventID;

  @JoinColumn(name = "event_id", nullable = false)
  @MapsId
  @OneToOne
  OperationsEvent operationsEvent;

  @OneToOne
  @JoinColumn(name = "timestamp_definition", nullable = false)
  TimestampDefinition timestampDefinition;

  @OneToOne
  @JoinColumn(name = "event_id", insertable = false, updatable = false)
  UnmappedEvent unmappedEvent;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @OneToMany
  @JoinColumn(name = "event_id", insertable = false, updatable = false)
  List<PendingEvent> pendingEvents;

  @Transient
  private boolean newRecord;

  @Override
  public UUID getId() {
    return this.eventID;
  }

  @Override
  public boolean isNew() {
    return this.newRecord || this.getId() == null;
  }
}
