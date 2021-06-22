package org.dcsa.ovs.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@Table("transport")
public class Transport {

    @Id
    @Column("id")
    private UUID transportID;

    @Column("transport_reference")
    @Size(max = 50)
    private String transportReference;

    @Column("transport_name")
    @Size(max = 100)
    private String transportName;

    @Column("mode_of_transport")
    @Size(max = 3)
    private String modeOfTransport;

    @JsonIgnore
    @Column("load_transport_call_id")
    private String loadTransportCallID;

    @JsonIgnore
    @Column("discharge_transport_call_id")
    private String dischargeTransportCallID;

    @JsonIgnore
    @Column("vessel")
    private String vesselIMONumber;


}
