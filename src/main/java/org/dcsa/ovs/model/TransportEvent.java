package org.dcsa.ovs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dcsa.ovs.model.enums.OperationsEventTypeCode;
import org.dcsa.ovs.model.enums.PortCallServiceTypeCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Table("operations_event")
@NoArgsConstructor
@Data
@JsonTypeName("TRANSPORT")
public class TransportEvent extends Event {

    @JsonProperty("transportCallID")
    @Column("transport_call_id")
    private UUID transportCallID;

    @JsonProperty("changeRemark")
    @Column("change_remark")
    private String changeRemark;

    @JsonProperty("delayReasonCode")
    @Column("delay_reason_code")
    private String delayReasonCode;


}


