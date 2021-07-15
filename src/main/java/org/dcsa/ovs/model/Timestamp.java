package org.dcsa.ovs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.dcsa.core.events.model.Party;
import org.dcsa.core.events.model.transferobjects.LocationTO;
import org.dcsa.ovs.model.enums.EventClassifierCode;
import org.dcsa.ovs.model.enums.OperationsEventTypeCode;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Data
@Table("timestamp")
public class Timestamp {

    @JsonProperty("publisher")
    private Party documentParty;

    private String facilitySMDGCode;

    @JsonProperty("UNLocationCode")
    private String UNLocationCode;

    private String modeOfTransport;
    private String vesselIMONumber;
    private EventClassifierCode eventClassifierCode;
    private OffsetDateTime eventDateTime;
    private OperationsEventTypeCode operationsEventTypeCode;

    @JsonProperty("eventLocation")
    private LocationTO location;

}
