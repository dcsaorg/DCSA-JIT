package org.dcsa.ovs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dcsa.core.model.AuditBase;
import org.dcsa.core.model.GetId;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.Pattern;

@Table("vessel")
@Data
@NoArgsConstructor
public class Vessel extends AuditBase implements GetId<String> {

    @JsonProperty("vesselIMONumber")
    @Column("vessel_imo_number")
    @Pattern(regexp = "[0-9]{7}")
    @Id
    private String id;

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