package org.dcsa.ovs.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dcsa.core.model.AuditBase;
import org.dcsa.core.model.GetId;
import org.dcsa.ovs.model.enums.EventClassifierCode;
import org.dcsa.ovs.model.enums.EventType;
import org.dcsa.ovs.model.enums.TransportEventCode;
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
        @JsonSubTypes.Type(value = PortCallEvent.class, name="PORTCALL")
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

    @JsonProperty("eventTypeCode")
    @Column("event_type_code")
    private String eventTypeCode;

    public void setEventClassifierCode(String eventClassifierCode) {
        this.eventClassifierCode = EventClassifierCode.valueOf(eventClassifierCode);
    }

    public void setEventClassifierCode(EventClassifierCode eventClassifierCode) {
        this.eventClassifierCode = eventClassifierCode;
    }

    public void setEventType(String eventType) {
        this.eventType = EventType.valueOf(eventType);
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
}
