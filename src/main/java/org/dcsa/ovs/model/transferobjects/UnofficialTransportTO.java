package org.dcsa.ovs.model.transferobjects;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dcsa.core.model.ForeignKey;
import org.dcsa.core.events.model.Transport;
import org.dcsa.core.events.model.Vessel;
import org.springframework.data.annotation.Transient;

@EqualsAndHashCode(callSuper = true)
@Data
// Unofficial Entity
public class UnofficialTransportTO extends Transport {

    @Transient
    @ForeignKey(fromFieldName = "loadTransportCallID", foreignFieldName = "transportCallID", viaJoinAlias = "ltc")
    private ShallowTransportCallTO loadTransportCall;

    @Transient
    @ForeignKey(fromFieldName = "dischargeTransportCallID", foreignFieldName = "transportCallID", viaJoinAlias = "dtc")
    private ShallowTransportCallTO dischargeTransportCall;

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
