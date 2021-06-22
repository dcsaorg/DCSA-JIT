package org.dcsa.ovs.model.transferobjects;

import org.dcsa.core.model.ForeignKey;
import org.dcsa.ovs.model.Transport;
import org.springframework.data.annotation.Transient;

public class TransportTO extends Transport {

    @Transient
    @ForeignKey(fromFieldName = "loadTransportCallID", foreignFieldName = "transportCallID")
    private TransportCallTO loadTransportCall;

    @Transient
    @ForeignKey(fromFieldName = "dischargeTransportCallID", foreignFieldName = "transportCallID")
    private TransportCallTO dischargeTransportCall;

    @Transient
    @ForeignKey(fromFieldName = "vesselIMONumber", foreignFieldName = "vesselIMONumber")
    private String vesselObject;

}
