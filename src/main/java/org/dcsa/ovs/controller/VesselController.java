package org.dcsa.ovs.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.ovs.model.TransportCall;
import org.dcsa.ovs.model.Vessel;
import org.dcsa.ovs.service.TransportCallService;
import org.dcsa.ovs.service.VesselService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "vessels", produces = {MediaType.APPLICATION_JSON_VALUE})
public class VesselController extends ExtendedBaseController<VesselService, Vessel, String> {

    private final VesselService vesselService;

    @Override
    public VesselService getService() {
        return vesselService;
    }

}
