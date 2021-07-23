package org.dcsa.ovs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.dcsa.core.events.model.enums.EventClassifierCode;
import org.dcsa.core.events.model.transferobjects.LocationTO;
import org.dcsa.core.events.model.transferobjects.PartyTO;
import org.dcsa.ovs.model.enums.OperationsEventTypeCode;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Data
@Table("timestamp")
public class Timestamp {
    private String facilitySMDGCode;
    private String facilityTypeCode;

    @JsonProperty("UNLocationCode")
    private String UNLocationCode;

    private String publisherRole;

    private String vesselIMONumber;
    private String modeOfTransport;
    private EventClassifierCode eventClassifierCode;
    private OffsetDateTime eventDateTime;
    private OperationsEventTypeCode operationsEventTypeCode;

    private String portCallServiceTypeCode;

    @Transient
    private LocationTO eventLocation;

    @Transient
    private PartyTO publisher;

    @Transient
    private LocationTO vesselPosition;
}
