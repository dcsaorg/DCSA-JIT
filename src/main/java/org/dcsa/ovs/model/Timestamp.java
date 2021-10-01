package org.dcsa.ovs.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.dcsa.core.events.model.enums.*;
import org.dcsa.core.events.model.transferobjects.LocationTO;
import org.dcsa.core.events.model.transferobjects.PartyTO;
import org.dcsa.core.validator.EnumSubset;
import org.dcsa.core.validator.ValidVesselIMONumber;
import org.dcsa.ovs.model.transferobjects.VesselPositionTO;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

@Data
@Table("timestamp")
public class Timestamp {
    @Deprecated
    @Size(max = 6)
    private String facilitySMDGCode;

    @NotNull
    @EnumSubset(anyOf = {"PBPL", "BRTH"})
    private FacilityTypeCode facilityTypeCode;

    @NotNull
    @Size(min = 1, max = 5)
    @JsonProperty("UNLocationCode")
    private String UNLocationCode;

    @NotNull
    private PublisherRole publisherRole;

    @ValidVesselIMONumber
    private String vesselIMONumber;

    private DCSATransportType modeOfTransport;

    @NotNull
    private EventClassifierCode eventClassifierCode;

    @NotNull
    private OffsetDateTime eventDateTime;

    @NotNull
    private OperationsEventTypeCode operationsEventTypeCode;

    private PortCallServiceTypeCode portCallServiceTypeCode;

    @NotNull
    @Size(max = 50)
    private String carrierVoyageNumber;

    @NotNull
    @Size(max = 5)
    private String carrierServiceCode;

    private Integer transportCallSequenceNumber;

    @Transient
    @Valid
    private LocationTO eventLocation;

    @NotNull
    @Transient
    @Valid
    private PartyTO publisher;

    @Transient
    @Valid
    private VesselPositionTO vesselPosition;

    @JsonIgnore
    public LocationTO getVesselPositionAsLocationTO() {
        if (vesselPosition == null) {
            return null;
        }
        return vesselPosition.toLocation();
    }

    private String remark;

    private String delayReasonCode;
}
