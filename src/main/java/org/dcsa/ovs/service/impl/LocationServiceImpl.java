package org.dcsa.ovs.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.Address;
import org.dcsa.core.events.model.Location;
import org.dcsa.core.events.model.transferobjects.LocationTO;
import org.dcsa.core.events.service.AddressService;
import org.dcsa.core.events.util.Util;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.ovs.repository.LocationRepository;
import org.dcsa.ovs.service.LocationService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class LocationServiceImpl extends ExtendedBaseServiceImpl<LocationRepository, Location, String> implements LocationService {
    private final LocationRepository locationRepository;
    private final AddressService addressService;

    @Override
    public LocationRepository getRepository() {
        return locationRepository;
    }

    public Mono<LocationTO> findPaymentLocationByShippingInstructionID(String shippingInstructionID) {
        return locationRepository.findPaymentLocationByShippingInstructionID(shippingInstructionID)
                .flatMap(this::getLocationTO);
    }

    @Override
    public Mono<LocationTO> ensureResolvable(LocationTO locationTO) {
        Address address = locationTO.getAddress();
        Mono<LocationTO> locationTOMono;
        if (address != null) {
            locationTOMono = addressService.ensureResolvable(address)
                    .doOnNext(locationTO::setAddress)
                    .thenReturn(locationTO);
        } else {
            locationTOMono = Mono.just(locationTO);
        }

        return locationTOMono
                .flatMap(locTo -> Util.createOrFindByContent(
                        locTo,
                        locationRepository::findByContent,
                        locTO -> this.create(locTO.toLocation())
                )).map(location -> location.toLocationTO(locationTO.getAddress()));
    }

    @Override
    public Mono<LocationTO> findTOById(String locationID) {
        return findById(locationID)
                .flatMap(this::getLocationTO);
    }

    private Mono<LocationTO> getLocationTO(Location location) {
        if (location.getAddressID() != null) {
            return addressService.findById(location.getAddressID())
                    .map(location::toLocationTO);
        }
        return Mono.just(location.toLocationTO(null));
    }
}
