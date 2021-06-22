package org.dcsa.ovs.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.ovs.model.base.AbstractTransportCall;
import org.springframework.data.relational.core.mapping.Table;

@Table("transport_call")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class TransportCall extends AbstractTransportCall {

}
