package org.dcsa.ovs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dcsa.ovs.model.enums.LocationType;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Table("operations_event")
@Data
@NoArgsConstructor
@JsonTypeName("TRANSPORT")
public class OperationsEvent extends Event {

    @JsonProperty("transportCallID")
    @Column("transport_call_id")
    private UUID transportCallID;

    @JsonProperty("eventCreatedDateTime")
    @Column("event_created_date_time")
    private OffsetDateTime creationDateTime;

    @JsonProperty("operationsEventTypeCode")
    @Column("operations_event_type_code")
    //@ToDO create OperationalEventTypeCodeEnum
    private String operationsEventTypeCode;

    @JsonProperty("publisher")
    @Column("publisher")
    private String publisher;

    @JsonProperty("publisherRole")
    @Column("publisher_role")
    private String publisherRole;

    @JsonProperty("eventLocation")
    @Column("event_location")
    private String eventLocation;

    @JsonProperty("portCallServiceTypeCode")
    @Column("port_call_service_type_code")
    private String portCallServiceTypeCode;

    @JsonProperty("facilityTypeCode")
    @Column("facility_type_code")
    private String facilityTypeCode;

    // @ToDo Clarify if still needed?
    /*
    @JsonProperty("comment")
    @Column("comment")
    private String comment;
    */

    @JsonProperty("delayReasonCode")
    @Column("delay_reason_code")
    private String delayReasonCode;

}


