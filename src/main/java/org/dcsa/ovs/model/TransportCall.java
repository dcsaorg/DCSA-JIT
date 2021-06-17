package org.dcsa.ovs.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dcsa.core.model.AuditBase;
import org.dcsa.core.model.GetId;
import org.dcsa.core.util.ValidationUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.Pattern;
import java.util.UUID;

@Table("transport_call")
@Data
@NoArgsConstructor
public class TransportCall extends AuditBase implements GetId<UUID> {

    @Id
    @JsonProperty("transportCallID")
    private UUID id;

    @JsonIgnore
    @Column("vessel")
    @Pattern(regexp = "[0-9]{7}")
    private String vesselIMONumber;

    public void setVesselIMONumber(String vesselIMONumber) {
        ValidationUtils.validateVesselIMONumber(vesselIMONumber);
        this.vesselIMONumber = vesselIMONumber;
    }

    /*
    @JsonProperty("vessel")
    @Transient
    private Vessel vessel;
    */

    @JsonProperty("transportCallSequenceNumber")
    @Column("transport_call_sequence_number")
    private Integer transportCallSequenceNumber;

    @JsonProperty("facilityTypeCode")
    @Column("facility_type_code")
    private String facilityTypeCode;

    @JsonProperty("facilityCode")
    @Column("facility_code")
    private String facilityCode;

    @JsonProperty("otherFacility")
    @Column("other_facility")
    private String otherFacility;

    @JsonProperty("locationId")
    @Column("location_id")
    private UUID locationId;
}
