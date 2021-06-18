package org.dcsa.ovs.model;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;

import java.util.UUID;

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
