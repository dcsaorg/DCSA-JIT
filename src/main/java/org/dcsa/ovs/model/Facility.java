package org.dcsa.ovs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.Size;
import java.util.UUID;

@Table("facility")
@Data
public class Facility {

    @Id
    @Column("id")
    private UUID facilityID;

    @Size(max = 100)
    @Column("facility_name")
    private String facilityName;

    @Size(max = 5)
    @Column("un_location_code")
    @JsonProperty("UNLocationCode")
    // Mismatch between JSON name and Java field name due to how the Core mapper works.
    private String unLocationCode;

    @Size(max = 4)
    @Column("facility_bic_code")
    private String facilityBICCode;

    @Size(max = 4)
    @Column("facility_smdg_code")
    private String facilitySMGDCode;

    @Column("location")
    private String locationID;

}
