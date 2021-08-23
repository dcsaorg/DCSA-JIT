package org.dcsa.ovs.controller;

import org.dcsa.core.events.controller.AbstractEventSubscriptionController;
import org.dcsa.core.events.service.EventSubscriptionTOService;
import org.dcsa.core.extendedrequest.ExtendedParameters;
import org.dcsa.ovs.model.transferobjects.OVSEventSubscriptionTO;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OVSEventSubscriptionTOController
    extends AbstractEventSubscriptionController<
        EventSubscriptionTOService<OVSEventSubscriptionTO>, OVSEventSubscriptionTO> {

  private final EventSubscriptionTOService<OVSEventSubscriptionTO> eventSubscriptionTOService;

  public OVSEventSubscriptionTOController(
      ExtendedParameters extendedParameters,
      R2dbcDialect r2dbcDialect,
      EventSubscriptionTOService<OVSEventSubscriptionTO> eventSubscriptionTOService) {
    super(extendedParameters, r2dbcDialect);
    this.eventSubscriptionTOService = eventSubscriptionTOService;
  }

  @Override
  public EventSubscriptionTOService<OVSEventSubscriptionTO> getService() {
    return this.eventSubscriptionTOService;
  }
}
