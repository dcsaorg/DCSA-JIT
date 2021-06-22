package org.dcsa.ovs.model.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.ovs.model.Event;
import org.dcsa.ovs.model.TransportCall;
import org.dcsa.ovs.model.enums.OperationsEventTypeCode;
import org.dcsa.ovs.model.enums.PortCallServiceTypeCode;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Table("operations_event")
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("OPERATIONS")
public class AbstractOperationsEvent extends Event {

    @JsonIgnore
    @Column("transport_call_id")
    private String transportCallID;

    @Column("event_created_date_time")
    private OffsetDateTime creationDateTime;

    @Column("operations_event_type_code")
    private OperationsEventTypeCode operationsEventTypeCode;

    @Column("publisher")
    private String publisher;

    @Column("publisher_role")
    private String publisherRole;

    @Column("publisher_code_list_provider")
    private String publisherCodeListProvider;

    @Column("event_location")
    private UUID eventLocation;

    @Column("port_call_service_type_code")
    private PortCallServiceTypeCode portCallServiceTypeCode;

    @Column("facility_type_code")
    private String facilityTypeCode;

    @Column("change_remark")
    private String changeRemark;

    @Column("delay_reason_code")
    private String delayReasonCode;

    @Transient
    private TransportCall transportCall;

}


