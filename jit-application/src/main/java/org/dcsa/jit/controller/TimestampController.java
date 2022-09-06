package org.dcsa.jit.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.jit.notifications.TimestampNotificationMailService;
import org.dcsa.jit.persistence.entity.OperationsEvent;
import org.dcsa.jit.service.TimestampService;
import org.dcsa.jit.transferobjects.IdentifyingCodeTO;
import org.dcsa.jit.transferobjects.TimestampTO;
import org.dcsa.skernel.errors.exceptions.ConcreteRequestErrorMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Stream;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "timestamps", produces = {MediaType.APPLICATION_JSON_VALUE})
public class TimestampController {

  private final TimestampService timestampService;
  private final TimestampNotificationMailService timestampNotificationMailService;

  @PostMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void create(@Valid @RequestBody TimestampTO timestamp) {
    timestamp.publisher().adjustIdentifyingCodesIfNmftaIsPresent();
    IdentifyingCodeTO invalid = Stream.ofNullable(timestamp.publisher().identifyingCodes())
        .flatMap(List::stream)
        .filter(c -> c.codeListResponsibleAgencyCode() != null)
        .filter(c -> !c.DCSAResponsibleAgencyCode().getLegacyAgencyCode().equals(c.codeListResponsibleAgencyCode()))
        .findAny()
        .orElse(null);
    if (invalid != null) {
      throw ConcreteRequestErrorMessageException.invalidInput("The codeListResponsibleAgencyCode \""
        + invalid.codeListResponsibleAgencyCode() + "\" does not match the DCSAResponsibleAgencyCode \""
        + invalid.DCSAResponsibleAgencyCode() + "\" (\""
        + invalid.DCSAResponsibleAgencyCode().getLegacyAgencyCode() + "\")");
    }
    OperationsEvent operationsEvent = timestampService.createAndRouteMessage(timestamp);
    timestampNotificationMailService.enqueueEmailNotificationForEvent(operationsEvent);
  }
}
