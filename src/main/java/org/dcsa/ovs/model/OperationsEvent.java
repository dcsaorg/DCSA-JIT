package org.dcsa.ovs.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.model.transferobjects.PartyTO;
import org.dcsa.ovs.model.base.AbstractOperationsEvent;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("operations_event")
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("OPERATIONS")
public class OperationsEvent extends AbstractOperationsEvent {


    @Column("publisher")
    private String publisherID;

    @Transient
    private PartyTO publisher;
}
