package org.dcsa.ovs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dcsa.ovs.model.enums.LocationType;
import org.dcsa.ovs.model.enums.TransportEventCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("portcall_event")
@Data
@NoArgsConstructor
@JsonTypeName("PORTCALL")
public class PortCallEvent extends Event {

    @JsonProperty("transportCallID")
    @Column("transport_call_id")
    private UUID transportCallID;

    @JsonProperty("locationType")
    @Column("location_type")
    private LocationType locationType;

    @JsonProperty("locationID")
    @Column("location_id")
    private String locationId;

    @JsonProperty("comment")
    @Column("comment")
    private String comment;

    @JsonProperty("delayReasonCode")
    @Column("delay_reason_code")
    private String delayReasonCode;



}


