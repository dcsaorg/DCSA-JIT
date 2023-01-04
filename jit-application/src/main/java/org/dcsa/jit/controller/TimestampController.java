package org.dcsa.jit.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.jit.service.TimestampService;
import org.dcsa.jit.transferobjects.IdentifyingCodeTO;
import org.dcsa.jit.transferobjects.TimestampTO;
import org.dcsa.skernel.errors.exceptions.ConcreteRequestErrorMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Stream;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "timestamps", produces = {MediaType.APPLICATION_JSON_VALUE})
public class TimestampController {

  private final TimestampService timestampService;

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
    if (timestamp.portVisitReference() != null && timestamp.portVisitReference().trim().isEmpty()) {
      throw ConcreteRequestErrorMessageException.invalidInput("The portVisitReference must be null or non-empty");
    }
    timestampService.createAndRouteMessage(timestamp);
  }
}
