package org.dcsa.ovs.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dcsa.ovs.model.enums.OperationsEventTypeCode;
import org.dcsa.ovs.model.enums.PortCallServiceTypeCode;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Table("operations_event")
@NoArgsConstructor
@Data
@JsonTypeName("OPERATIONS")
public class OperationsEvent extends Event {

    @JsonIgnore
    @JsonProperty("transportCallID")
    @Column("transport_call_id")
    private UUID transportCallID;

    @JsonProperty("eventCreatedDateTime")
    @Column("event_created_date_time")
    private OffsetDateTime creationDateTime;

    //  on Core for test purposes put to String
    @JsonProperty("operationsEventTypeCode")
    @Column("operations_event_type_code")
    private OperationsEventTypeCode operationsEventTypeCode;

    @JsonProperty("publisher")
    @Column("publisher")
    private String publisher;

    @JsonProperty("publisherRole")
    @Column("publisher_role")
    private String publisherRole;

    @JsonProperty("publisherCodeListProvider")
    @Column("publisher_code_list_provider")
    private String publisherCodeListProvider;

    @JsonProperty("eventLocation")
    @Column("event_location")
    private String eventLocation;
    public void setEventLocation(UUID id){}

    //  on Core for test purposes put to String
    @JsonProperty("portCallServiceTypeCode")
    @Column("port_call_service_type_code")
    private PortCallServiceTypeCode portCallServiceTypeCode;

    @JsonProperty("facilityTypeCode")
    @Column("facility_type_code")
    private String facilityTypeCode;

    @JsonProperty("changeRemark")
    @Column("change_remark")
    private String changeRemark;

    @JsonProperty("delayReasonCode")
    @Column("delay_reason_code")
    private String delayReasonCode;

    @JsonProperty("transportCall")
    @Transient
    private TransportCall transportCall;


}


