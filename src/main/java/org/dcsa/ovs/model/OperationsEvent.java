package org.dcsa.ovs.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dcsa.ovs.model.base.AbstractOperationsEvent;
import org.springframework.data.relational.core.mapping.Table;

@Table("operations_event")
@NoArgsConstructor
@Data
@JsonTypeName("OPERATIONS")
public class OperationsEvent extends AbstractOperationsEvent {

}
