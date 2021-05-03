package org.dcsa.ovs.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dcsa.core.model.AuditBase;
import org.dcsa.core.model.GetId;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.persistence.Id;
import javax.validation.constraints.Pattern;
import java.util.UUID;

@Table("vessel")
@Data
@NoArgsConstructor
public class Vessel extends AuditBase {

    @Id
    @JsonProperty("vesselIMONumber")
    @Column("vessel_imo_number")
    @Pattern(regexp = "[0-9]{7}")
    private String vesselIMONumber;

    @JsonProperty("vesselName")
    @Column("vessel_name")
    private String vesselName;

    @JsonProperty("vesselFlag")
    @Column("vessel_flag")
    private String vesselFlag;

    @JsonProperty("vesselCallSignNumber")
    @Column("vessel_call_sign_number")
    private String vesselCallSignNumber;

    @JsonProperty("vesselOperatorCarrierId")
    @Column("vessel_operator_carrier_id")
    private String vesselOperatorCarrierId;


}
