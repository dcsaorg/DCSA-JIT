package org.dcsa.jit.controller;

import org.dcsa.core.events.controller.AbstractEventSubscriptionController;
import org.dcsa.core.events.service.EventSubscriptionTOService;
import org.dcsa.core.extendedrequest.ExtendedParameters;
import org.dcsa.jit.model.transferobjects.JITEventSubscriptionTO;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JITEventSubscriptionTOController
    extends AbstractEventSubscriptionController<
        EventSubscriptionTOService<JITEventSubscriptionTO>, JITEventSubscriptionTO> {

  private final EventSubscriptionTOService<JITEventSubscriptionTO> eventSubscriptionTOService;

  public JITEventSubscriptionTOController(
      ExtendedParameters extendedParameters,
      R2dbcDialect r2dbcDialect,
      EventSubscriptionTOService<JITEventSubscriptionTO> eventSubscriptionTOService) {
    super(extendedParameters, r2dbcDialect);
    this.eventSubscriptionTOService = eventSubscriptionTOService;
  }

  @Override
  public EventSubscriptionTOService<JITEventSubscriptionTO> getService() {
    return this.eventSubscriptionTOService;
  }
}