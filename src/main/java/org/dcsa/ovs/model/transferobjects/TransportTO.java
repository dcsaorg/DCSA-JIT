package org.dcsa.ovs.model.transferobjects;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dcsa.core.model.ForeignKey;
import org.dcsa.ovs.model.Transport;
import org.dcsa.ovs.model.Vessel;
import org.springframework.data.annotation.Transient;

@EqualsAndHashCode(callSuper = true)
@Data
public class TransportTO extends Transport {

    @Transient
    @ForeignKey(fromFieldName = "loadTransportCallID", foreignFieldName = "transportCallID", viaJoinAlias = "load_transport_call")
    private TransportCallTO loadTransportCall;

    @Transient
    @ForeignKey(fromFieldName = "dischargeTransportCallID", foreignFieldName = "transportCallID", viaJoinAlias = "discharge_transport_call")
    private TransportCallTO dischargeTransportCall;

    @Transient
    @ForeignKey(fromFieldName = "vesselIMONumber", foreignFieldName = "vesselIMONumber")
    private Vessel vessel;

    public void setVessel(Vessel vessel) {
        if (vessel != null) {
            this.setVesselIMONumber(vessel.getVesselIMONumber());
        }
        this.vessel = vessel;
    }
}
