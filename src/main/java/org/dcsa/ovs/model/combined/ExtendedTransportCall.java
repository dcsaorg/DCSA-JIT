package org.dcsa.ovs.model.combined;

import org.dcsa.core.model.JoinedWithModel;
import org.dcsa.core.model.ModelClass;
import org.dcsa.core.model.PrimaryModel;
import org.dcsa.ovs.model.TransportCall;
import org.dcsa.ovs.model.Vessel;
import org.springframework.data.relational.core.sql.Join;

@PrimaryModel(TransportCall.class)
@JoinedWithModel(lhsFieldName = "vessel_imo_number",
        rhsModel = Vessel.class, rhsFieldName = "vessel_imo_number", joinType = Join.JoinType.LEFT_OUTER_JOIN)

public class ExtendedTransportCall extends TransportCall {

    @ModelClass(value = TransportCall.class, fieldName = "vesselIMONumber")
    private String vesselIMONumber;



}
