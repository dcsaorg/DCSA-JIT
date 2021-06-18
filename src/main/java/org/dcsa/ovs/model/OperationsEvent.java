package org.dcsa.ovs.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.ovs.model.base.AbstractOperationsEvent;
import org.springframework.data.relational.core.mapping.Table;

@Table("operations_event")
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("OPERATIONS")
public class OperationsEvent extends AbstractOperationsEvent {

}
