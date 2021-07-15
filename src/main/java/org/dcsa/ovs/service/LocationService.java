package org.dcsa.ovs.service;

import org.dcsa.core.events.model.Location;
import org.dcsa.core.events.model.transferobjects.LocationTO;
import org.dcsa.core.service.ExtendedBaseService;
import reactor.core.publisher.Mono;

public interface LocationService extends ExtendedBaseService<Location, String> {

    Mono<LocationTO> ensureResolvable(LocationTO locationTO);

    Mono<LocationTO> findPaymentLocationByShippingInstructionID(String shippingInstructionID);

    Mono<LocationTO> findTOById(String locationID);
}
