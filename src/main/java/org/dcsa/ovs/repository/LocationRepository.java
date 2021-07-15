package org.dcsa.ovs.repository;

import org.dcsa.core.events.model.Address;
import org.dcsa.core.events.model.Location;
import org.dcsa.core.events.model.transferobjects.LocationTO;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface LocationRepository extends ExtendedRepository<Location, String> {
    @Query("SELECT location.*"
            + "  FROM location"
            + "  JOIN shipping_instruction ON (location.id=shipping_instruction.invoice_payable_at)"
            + " WHERE shipping_instruction.id = :shippingInstructionID"
    )
    Mono<Location> findPaymentLocationByShippingInstructionID(String shippingInstructionID);

    Mono<Location> findByAddressIDAndLocationNameAndLatitudeAndLongitudeAndUnLocationCode(
            UUID addressID,
            String locationName,
            String latitude,
            String longitude,
            String unLocationCode
    );

    default Mono<Location> findByContent(LocationTO locationTO) {
        Address address = locationTO.getAddress();
        UUID addressID = address != null ? address.getId() : null;
        return findByAddressIDAndLocationNameAndLatitudeAndLongitudeAndUnLocationCode(
                addressID,
                locationTO.getLocationName(),
                locationTO.getLatitude(),
                locationTO.getLongitude(),
                locationTO.getUnLocationCode()
        );
    }
}
