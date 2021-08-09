package org.dcsa.ovs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.dcsa.core.events.model.enums.EventClassifierCode;
import org.dcsa.core.events.model.enums.OperationsEventTypeCode;
import org.dcsa.core.events.model.enums.PortCallServiceTypeCode;
import org.dcsa.core.events.model.transferobjects.LocationTO;
import org.dcsa.core.events.model.transferobjects.PartyTO;
import org.dcsa.core.events.model.enums.FacilityTypeCode;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

@Data
@Table("timestamp")
public class Timestamp {
    @Size(max = 6)
    private String facilitySMDGCode;

    @NotNull
    private FacilityTypeCode facilityTypeCode;

    @NotNull
    @JsonProperty("UNLocationCode")
    private String UNLocationCode;

    @NotNull
    private String publisherRole;

    @NotNull
    private String vesselIMONumber;

    private String modeOfTransport;
    @NotNull
    private EventClassifierCode eventClassifierCode;

    @NotNull
    private OffsetDateTime eventDateTime;
    
    @NotNull
    private OperationsEventTypeCode operationsEventTypeCode;

    private PortCallServiceTypeCode portCallServiceTypeCode;

    @Transient
    private LocationTO eventLocation;

    @NotNull
    @Transient
    private PartyTO publisher;

    @Transient
    private LocationTO vesselPosition;
}
