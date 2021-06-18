package org.dcsa.ovs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.model.AuditBase;
import org.dcsa.core.model.GetId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.UUID;

@Table("vessel")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Vessel extends AuditBase implements GetId<String> {

    @JsonProperty("vesselIMONumber")
    @Column("vessel_imo_number")
    @Pattern(regexp = "[0-9]{7}")
    @Id
    private String id;

    @Size(max = 35)
    @Column("vessel_name")
    private String vesselName;

    @Size(max = 2)
    @Column("vessel_flag")
    private String vesselFlag;

    @Size(max = 10)
    @Column("vessel_call_sign_number")
    private String vesselCallSignNumber;

    @Column("vessel_operator_carrier_id")
    private UUID vesselOperatorCarrierID;

    @Transient
    private Double teu;

    @Transient
    private String serviceNameCode;

}