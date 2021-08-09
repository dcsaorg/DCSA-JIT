package org.dcsa.ovs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.dcsa.core.events.model.enums.*;
import org.dcsa.core.events.model.transferobjects.LocationTO;
import org.dcsa.core.events.model.transferobjects.PartyTO;
import org.dcsa.core.util.ValidationUtils;
import org.dcsa.core.validator.EnumSubset;
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
    @EnumSubset(anyOf = {"PBPL","BRTH"})
    private FacilityTypeCode facilityTypeCode;

    @NotNull
    @Size(min = 1, max = 5)
    @JsonProperty("UNLocationCode")
    private String UNLocationCode;

    @NotNull
    private PublisherRole publisherRole;

    @NotNull
    @Size(min = 7, max = 7)
    private String vesselIMONumber;

    public void setVesselIMONumber(String vesselIMONumber) {
        ValidationUtils.validateVesselIMONumber(vesselIMONumber);
        this.vesselIMONumber = vesselIMONumber;
    }

    private DCSATransportType modeOfTransport;

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
