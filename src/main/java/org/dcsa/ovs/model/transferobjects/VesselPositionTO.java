package org.dcsa.ovs.model.transferobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.model.transferobjects.LocationTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VesselPositionTO {

    @NotBlank
    @Size(max = 10)
    private String latitude;

    @NotBlank
    @Size(max = 11)
    private String longitude;

    public LocationTO toLocation() {
        LocationTO locationTO = new LocationTO();
        locationTO.setLatitude(latitude);
        locationTO.setLongitude(longitude);
        return locationTO;
    }
}
