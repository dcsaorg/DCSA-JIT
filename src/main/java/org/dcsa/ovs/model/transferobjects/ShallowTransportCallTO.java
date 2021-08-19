package org.dcsa.ovs.model.transferobjects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.model.Transport;
import org.dcsa.core.events.model.Vessel;
import org.dcsa.core.events.model.base.AbstractTransportCall;
import org.dcsa.core.events.model.enums.FacilityCodeListProvider;
import org.dcsa.core.events.model.transferobjects.FacilityTO;
import org.dcsa.core.events.model.transferobjects.LocationTO;
import org.dcsa.core.model.ForeignKey;
import org.dcsa.core.model.JoinedWithModel;
import org.dcsa.core.model.MapEntity;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.sql.Join;

import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
/*
 * Variant of "shallow'er" variant of TransportCallTO that does not map in Transport (or fields below it).
 *
 * This avoids a "cycle" of "Transport -> 2x Transport Call -> 2x (copies of the first) Transport" in the
 * SQL, which in turn avoids "findById" returning multiple rows for a given transport.
 */
public class ShallowTransportCallTO extends AbstractTransportCall {

    @Transient
    private String carrierServiceCode;

    @Transient
    private String carrierVoyageNumber;

    @Size(max = 5)
    @Transient
    @JsonProperty("UNLocationCode")
    private String UNLocationCode;

    @Size(max = 6)
    @Transient
    private String facilityCode;

    @Transient
    private FacilityCodeListProvider facilityCodeListProvider;

    @ForeignKey(fromFieldName = "facilityID", foreignFieldName = "facilityID", joinType = Join.JoinType.LEFT_OUTER_JOIN)
    @JsonIgnore
    @Transient
    private FacilityTO facility;

    @ForeignKey(fromFieldName = "locationID", foreignFieldName = "id", joinType = Join.JoinType.LEFT_OUTER_JOIN)
    @Transient
    private LocationTO location;

    @Transient
    private String modeOfTransport;

    public LocationTO getLocation() {
        if (location != null && !location.isNullLocation()) {
            return location;
        }
        if (facility != null) {
            LocationTO facilityLocation = facility.getLocation();
            if (facilityLocation != null && !facilityLocation.isNullLocation()) {
                return facilityLocation;
            }
        }
        return null;
    }

    public void setFacility(FacilityTO facility) {
        if (facility != null && !facility.isNullFacility()) {
            UNLocationCode = facility.getUnLocationCode();
            if (facility.getFacilitySMGDCode() != null) {
                facilityCode = facility.getFacilitySMGDCode();
                facilityCodeListProvider = FacilityCodeListProvider.SMDG;
            } else if (facility.getFacilityBICCode() != null) {
                facilityCode = facility.getFacilityBICCode();
                facilityCodeListProvider = FacilityCodeListProvider.BIC;
            } else {
                throw new IllegalArgumentException("Unsupported facility code list provider.");
            }
        } else {
            facilityCode = null;
            facilityCodeListProvider = null;
            UNLocationCode = null;
        }
        this.facility = facility;
    }
}
