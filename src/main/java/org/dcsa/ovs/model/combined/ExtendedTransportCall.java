package org.dcsa.ovs.model.combined;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.model.JoinedWithModel;
import org.dcsa.core.model.ModelClass;
import org.dcsa.core.model.PrimaryModel;
import org.dcsa.ovs.model.TransportCall;
import org.dcsa.ovs.model.Vessel;
import org.springframework.data.relational.core.sql.Join;

import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@PrimaryModel(TransportCall.class)
@JoinedWithModel(lhsModel = TransportCall.class, lhsFieldName = "vesselIMONumber",
        rhsModel = Vessel.class, rhsFieldName = "vesselIMONumber", joinType = Join.JoinType.LEFT_OUTER_JOIN)

public class ExtendedTransportCall extends TransportCall {

    //LoadVessel
    @JsonIgnore
    @ModelClass(value = Vessel.class, fieldName = "vesselIMONumber")
    private int vesselMONumber;

    @JsonIgnore
    @ModelClass(value = Vessel.class, fieldName = "vesselName")
    private String vesselName;

    @JsonIgnore
    @ModelClass(value = Vessel.class, fieldName = "vesselFlag")
    private String vesselFlag;

    @JsonIgnore
    @ModelClass(value = Vessel.class, fieldName = "vesselCallSignNumber")
    private String vesselCallSignNumber;

    @JsonIgnore
    @ModelClass(value = Vessel.class, fieldName = "vesselOperatorCarrierId")
    private String vesselOperatorCarrierId;



    public void setVesselMONumber(String vesselIMONumber){}
    public void setVesselOperatorCarrierId(UUID vesselOperatorCarrierId){};

    public Vessel getVessel(){
        Vessel vessel = new Vessel();
        vessel.setVesselIMONumber(getVesselIMONumber());
        vessel.setVesselName(getVesselName());
        vessel.setVesselFlag(getVesselFlag());
        vessel.setVesselCallSignNumber(getVesselCallSignNumber());
        vessel.setVesselOperatorCarrierId(getVesselOperatorCarrierId());


        return vessel;
    }




}
