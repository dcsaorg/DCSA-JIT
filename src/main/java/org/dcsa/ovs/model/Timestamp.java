package org.dcsa.ovs.model;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("timestamp")
public class Timestamp {

    private String facilitySMDGCode;
    private String UNLocationCode;

    private String modeOfTransport;
    private String vesselIMONumber;
}
