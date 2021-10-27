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
import java.util.Set;

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

    public void ensurePhaseTypeIsDefined() {
        if (portCallPhaseTypeCode != null) {
            return;
        }
        if (portCallServiceTypeCode != null) {
            Set<PortCallPhaseTypeCode> validPhases = portCallServiceTypeCode.getValidPhases();
            if (validPhases.size() == 1) {
                portCallPhaseTypeCode = validPhases.iterator().next();
            }
        } else if (facilityTypeCode != null) {
            switch (facilityTypeCode) {
                case BRTH:
                    if (operationsEventTypeCode == OperationsEventTypeCode.ARRI) {
                        if (eventClassifierCode == EventClassifierCode.ACT) {
                            portCallPhaseTypeCode = PortCallPhaseTypeCode.ALGS;
                        } else {
                            portCallPhaseTypeCode = PortCallPhaseTypeCode.INBD;
                        }
                    }
                    if (operationsEventTypeCode == OperationsEventTypeCode.DEPA) {
                        if (eventClassifierCode == EventClassifierCode.ACT) {
                            portCallPhaseTypeCode = PortCallPhaseTypeCode.OUTB;
                        } else {
                            portCallPhaseTypeCode = PortCallPhaseTypeCode.ALGS;
                        }
                    }
                    break;
                case PBPL:
                    portCallPhaseTypeCode = PortCallPhaseTypeCode.INBD;
                    break;
            }
        }
        if (portCallPhaseTypeCode == null) {
            throw new IllegalStateException("Ambiguous timestamp");
        }
    }
}
