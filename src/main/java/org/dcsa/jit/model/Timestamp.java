package org.dcsa.jit.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.dcsa.core.events.model.enums.*;
import org.dcsa.core.validator.EnumSubset;
import org.dcsa.skernel.model.enums.FacilityTypeCode;
import org.dcsa.skernel.model.enums.PartyFunction;
import org.dcsa.skernel.model.transferobjects.LocationTO;
import org.dcsa.skernel.validator.ValidLocationSubtype;
import org.dcsa.skernel.validator.ValidVesselIMONumber;
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

    @EnumSubset(anyOf = {"PBPL", "BRTH"})
    private FacilityTypeCode facilityTypeCode;

    @NotNull
    @Size(min = 1, max = 5)
    @JsonProperty("UNLocationCode")
    private String UNLocationCode;

    @NotNull
    @EnumSubset(anyOf = {"CA", "AG", "VSL", "ATH", "PLT", "TR", "TWG", "BUK", "LSH"})
    private PartyFunction publisherRole;

    @ValidVesselIMONumber
    private String vesselIMONumber;

    @EnumSubset(anyOf = {"VESSEL"})
    private DCSATransportType modeOfTransport;

    @NotNull
    private EventClassifierCode eventClassifierCode;

    @NotNull
    private OffsetDateTime eventDateTime;

    @NotNull
    private OperationsEventTypeCode operationsEventTypeCode;

    private PortCallPhaseTypeCode portCallPhaseTypeCode;

    private PortCallServiceTypeCode portCallServiceTypeCode;

    @Size(max = 50)
    @Deprecated
    private String carrierVoyageNumber;

    @Size(max = 50)
    private String exportVoyageNumber;

    @Size(max = 50)
    private String importVoyageNumber;

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
    private JITPartyTO publisher;

    @ValidLocationSubtype(anyOf = ValidLocationSubtype.LocationSubtype.GEO_COORDINATE)
    @Transient
    @Valid
    private LocationTO vesselPosition;

    private String remark;

    private String delayReasonCode;

}
