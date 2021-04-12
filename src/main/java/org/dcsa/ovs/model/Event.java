package org.dcsa.ovs.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dcsa.core.model.AuditBase;
import org.dcsa.core.model.GetId;
import org.dcsa.ovs.model.enums.EventClassifierCode;
import org.dcsa.ovs.model.enums.EventType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Table("aggregated_events")
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "eventType",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TransportEvent.class, name="TRANSPORT"),
        @JsonSubTypes.Type(value = OperationsEvent.class, name="OPERATIONS"),
        })
public class Event extends AuditBase implements GetId<UUID> {

    @Id
    @JsonProperty("eventID")
    @Column("event_id")
    private UUID id;

    @JsonProperty("eventType")
    @Column("event_type")
    private EventType eventType;

    @JsonProperty("eventDateTime")
    @Column("event_date_time")
    private OffsetDateTime eventDateTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public OffsetDateTime getEventDateTime() {
        return eventDateTime;
    }

    @JsonProperty("eventClassifierCode")
    @Column("event_classifier_code")
    private EventClassifierCode eventClassifierCode;


}
