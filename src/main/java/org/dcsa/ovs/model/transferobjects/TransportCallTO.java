package org.dcsa.ovs.model.transferobjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.dcsa.core.model.ForeignKey;
import org.dcsa.ovs.model.Facility;
import org.dcsa.ovs.model.TransportCall;
import org.dcsa.ovs.model.base.AbstractTransportCall;
import org.dcsa.ovs.model.enums.FacilityCodeListProvider;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class TransportCallTO extends AbstractTransportCall {

    @Size(max = 5)
    @Transient
    @JsonProperty("UNLocationCode")
    private String UNLocationCode;

    @Size(max = 6)
    @Transient
    private String facilityCode;

    @Transient
    private FacilityCodeListProvider facilityCodeListProvider;

    @ForeignKey(fromFieldName = "facilityID", foreignFieldName = "facilityID")
    @Transient
    private Facility facility;

    public void setFacility(Facility facility) {
        if (facility != null) {
            UNLocationCode = facility.getUnLocationCode();
            if (facility.getFacilitySMGDCode() != null) {
                facilityCode = facility.getFacilitySMGDCode();
                facilityCodeListProvider = FacilityCodeListProvider.SMDG;
            } else {
                facilityCode = facility.getFacilityBICCode();
                facilityCodeListProvider = FacilityCodeListProvider.BIC;
            }
        } else {
            facilityCode = null;
            facilityCodeListProvider = null;
            UNLocationCode = null;
        }
        this.facility = facility;
    }
}
