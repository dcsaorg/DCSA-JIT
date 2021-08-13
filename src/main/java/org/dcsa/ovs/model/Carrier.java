package org.dcsa.ovs.model;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("carrier")
@Data
public class Carrier {

    private UUID id;

    @Column("carrier_name")
    private String carrierName;

    @Column("smdg_code")
    private String smdgCode;

    @Column("nmfta_code")
    private String nmftaCode;

}
