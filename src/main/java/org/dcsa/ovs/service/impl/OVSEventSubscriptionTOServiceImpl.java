package org.dcsa.ovs.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.EventSubscription;
import org.dcsa.core.events.model.base.AbstractEventSubscription;
import org.dcsa.core.events.model.enums.*;
import org.dcsa.core.events.repository.EventSubscriptionRepository;
import org.dcsa.core.events.service.EventSubscriptionService;
import org.dcsa.core.events.service.impl.EventSubscriptionTOServiceImpl;
import org.dcsa.core.exception.UpdateException;
import org.dcsa.core.util.MappingUtils;
import org.dcsa.ovs.model.transferobjects.OVSEventSubscriptionTO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OVSEventSubscriptionTOServiceImpl
    extends EventSubscriptionTOServiceImpl<OVSEventSubscriptionTO, EventSubscriptionService> {

  private static final String ALL_ALLOWED_EVENT_TYPES =
      EventType.TRANSPORT.name() + "," + EventType.OPERATIONS.name();

  private final EventSubscriptionService eventSubscriptionService;
  private final EventSubscriptionRepository eventSubscriptionRepository;

  @Override
  protected EventSubscriptionService getService() {
    return this.eventSubscriptionService;
  }

  @Override
  public Mono<OVSEventSubscriptionTO> create(OVSEventSubscriptionTO eventSubscriptionTO) {
    return eventSubscriptionService
        .create(eventSubscriptionTOToEventSubscription.apply(eventSubscriptionTO))
        .flatMap(
            eventSubscription -> {
              eventSubscriptionTO.setSubscriptionID(eventSubscription.getSubscriptionID());
              return createEventTypes(eventSubscriptionTO)
                  .then(createTransportEventType(eventSubscriptionTO))
                  .then(createOperationsEventType(eventSubscriptionTO))
                  .thenReturn(eventSubscriptionTO);
            });
  }

  // ToDo : replace this with mapstruct
  private final Function<OVSEventSubscriptionTO, EventSubscription>
      eventSubscriptionTOToEventSubscription =
          esTo -> {
            EventSubscription eventSubscription = new EventSubscription();
            eventSubscription.setSubscriptionID(esTo.getSubscriptionID());
            eventSubscription.setCallbackUrl(esTo.getCallbackUrl());
            eventSubscription.setSecret(esTo.getSecret());
            eventSubscription.setCarrierServiceCode(esTo.getCarrierServiceCode());
            eventSubscription.setCarrierVoyageNumber(esTo.getCarrierVoyageNumber());
            eventSubscription.setVesselIMONumber(esTo.getVesselIMONumber());
            eventSubscription.setTransportCallID(esTo.getTransportCallID());
            return eventSubscription;
          };

  private Mono<OVSEventSubscriptionTO> createEventTypes(
      OVSEventSubscriptionTO eventSubscriptionTO) {

    String eventTypes;

    if (!StringUtils.hasLength(eventSubscriptionTO.getEventType())) {
      eventTypes = ALL_ALLOWED_EVENT_TYPES;
      eventSubscriptionTO.setEventType(eventTypes);
    } else {
      eventTypes = eventSubscriptionTO.getEventType();
    }
    return Flux.fromIterable(stringToEventTypeList.apply(eventTypes))
        .concatMap(
            eventType ->
                eventSubscriptionRepository.insertEventTypeForSubscription(
                    eventSubscriptionTO.getSubscriptionID(), eventType))
        .then(Mono.just(eventSubscriptionTO));
  }

  private Mono<Void> createTransportEventType(OVSEventSubscriptionTO eventSubscriptionTO) {
    String transportEventTypeCode = eventSubscriptionTO.getTransportEventTypeCode();
    if (StringUtils.hasLength(transportEventTypeCode) && transportEventTypeCode.contains(",")) {
      return Flux.fromIterable(
              Arrays.stream(transportEventTypeCode.split(",")).collect(Collectors.toList()))
          .flatMap(
              e ->
                  eventSubscriptionRepository.insertTransportEventTypeForSubscription(
                      eventSubscriptionTO.getSubscriptionID(), TransportEventTypeCode.valueOf(e)))
          .then();
    } else if (StringUtils.hasLength(transportEventTypeCode)) {
      return eventSubscriptionRepository.insertTransportEventTypeForSubscription(
          eventSubscriptionTO.getSubscriptionID(),
          TransportEventTypeCode.valueOf(transportEventTypeCode));
    }
    return Mono.empty();
  }

  private Mono<Void> createOperationsEventType(OVSEventSubscriptionTO eventSubscriptionTO) {
    String operationsEventTypeCode = eventSubscriptionTO.getOperationsEventTypeCode();
    if (StringUtils.hasLength(operationsEventTypeCode) && operationsEventTypeCode.contains(",")) {
      return Flux.fromIterable(
              Arrays.stream(operationsEventTypeCode.split(",")).collect(Collectors.toList()))
          .flatMap(
              e ->
                  eventSubscriptionRepository.insertOperationsEventTypeForSubscription(
                      eventSubscriptionTO.getSubscriptionID(), OperationsEventTypeCode.valueOf(e)))
          .then();
    } else if (StringUtils.hasLength(operationsEventTypeCode)) {
      return eventSubscriptionRepository.insertOperationsEventTypeForSubscription(
          eventSubscriptionTO.getSubscriptionID(),
          OperationsEventTypeCode.valueOf(operationsEventTypeCode));
    }
    return Mono.empty();
  }

  @Override
  public Mono<OVSEventSubscriptionTO> update(OVSEventSubscriptionTO eventSubscriptionTO) {
    if (eventSubscriptionTO.getSecret() != null) {
      return Mono.error(
          new UpdateException(
              "Please omit the \"secret\" attribute.  If you want to change the"
                  + " secret, please use the dedicated secret endpoint"
                  + " (\"PUT .../event-subscriptions/"
                  + eventSubscriptionTO.getSubscriptionID()
                  + "/secret\")."));
    }
    return eventSubscriptionRepository
        .deleteEventTypesForSubscription(eventSubscriptionTO.getSubscriptionID())
        .thenReturn(eventSubscriptionTO)
        .map(eventSubscriptionTOToEventSubscription)
        .flatMap(
            updated ->
                eventSubscriptionService
                    .findById(updated.getSubscriptionID())
                    .map(
                        original -> {
                          updated.setSecret(original.getSecret());
                          updated.copyInternalFieldsFrom(original);
                          return updated;
                        }))
        .flatMap(eventSubscriptionService::update)
        .flatMap(ignored -> createEventTypes(eventSubscriptionTO));
  }

  @Override
  protected Flux<OVSEventSubscriptionTO> mapManyD2TO(
      Flux<EventSubscription> eventSubscriptionFlux) {
    return eventSubscriptionFlux
        .map(eventSubscriptionToEventSubscriptionTo)
        .collectList()
        .flatMapMany(
            eventSubscriptionList -> {
              Map<UUID, OVSEventSubscriptionTO> id2subscription =
                  eventSubscriptionList.stream()
                      .collect(
                          Collectors.toMap(
                              AbstractEventSubscription::getSubscriptionID, Function.identity()));
              return Flux.fromIterable(eventSubscriptionList)
                  .map(AbstractEventSubscription::getSubscriptionID)
                  .buffer(MappingUtils.SQL_LIST_BUFFER_SIZE)
                  .concatMap(eventSubscriptionRepository::findEventTypesForSubscriptionIDIn)
                  .doOnNext(
                      eventSubscriptionEventType -> {
                        OVSEventSubscriptionTO subscriptionTO =
                            id2subscription.get(eventSubscriptionEventType.getSubscriptionID());
                        assert subscriptionTO != null;
                        if (!StringUtils.hasLength(subscriptionTO.getEventType())) {
                          subscriptionTO.setEventType(
                              eventSubscriptionEventType.getEventType().name());
                        } else if (StringUtils.hasLength(subscriptionTO.getEventType())
                            && !subscriptionTO.getEventType().endsWith(",")) {
                          subscriptionTO.setEventType(
                              subscriptionTO
                                  .getEventType()
                                  .concat(",")
                                  .concat(eventSubscriptionEventType.getEventType().name()));
                        }
                      })
                  .thenMany(Flux.fromIterable(eventSubscriptionList))
                  .map(AbstractEventSubscription::getSubscriptionID)
                  .buffer(MappingUtils.SQL_LIST_BUFFER_SIZE)
                  .concatMap(
                      eventSubscriptionRepository::findTransportEventTypesForSubscriptionIDIn)
                  .doOnNext(
                      esTransportEventType -> {
                        OVSEventSubscriptionTO subscriptionTO =
                            id2subscription.get(esTransportEventType.getSubscriptionID());
                        assert subscriptionTO != null;
                        if (!StringUtils.hasLength(subscriptionTO.getTransportEventTypeCode())) {
                          subscriptionTO.setTransportEventTypeCode(
                              esTransportEventType.getTransportEventTypeCode().name());
                        } else if (StringUtils.hasLength(subscriptionTO.getTransportEventTypeCode())
                            && !subscriptionTO.getTransportEventTypeCode().endsWith(",")) {
                          subscriptionTO.setTransportEventTypeCode(
                              subscriptionTO
                                  .getTransportEventTypeCode()
                                  .concat(",")
                                  .concat(esTransportEventType.getTransportEventTypeCode().name()));
                        }
                      })
                  .thenMany(Flux.fromIterable(eventSubscriptionList))
                  .map(AbstractEventSubscription::getSubscriptionID)
                  .buffer(MappingUtils.SQL_LIST_BUFFER_SIZE)
                  .concatMap(
                      eventSubscriptionRepository::findOperationsEventTypesForSubscriptionIDIn)
                  .doOnNext(
                      esOperationsEventType -> {
                        OVSEventSubscriptionTO subscriptionTO =
                            id2subscription.get(esOperationsEventType.getSubscriptionID());
                        assert subscriptionTO != null;
                        if (!StringUtils.hasLength(subscriptionTO.getOperationsEventTypeCode())) {
                          subscriptionTO.setOperationsEventTypeCode(
                              esOperationsEventType.getOperationsEventTypeCode().name());
                        } else if (StringUtils.hasLength(
                                subscriptionTO.getOperationsEventTypeCode())
                            && !subscriptionTO.getOperationsEventTypeCode().endsWith(",")) {
                          subscriptionTO.setOperationsEventTypeCode(
                              subscriptionTO
                                  .getOperationsEventTypeCode()
                                  .concat(",")
                                  .concat(
                                      esOperationsEventType.getOperationsEventTypeCode().name()));
                        }
                      })
                  .thenMany(Flux.fromIterable(eventSubscriptionList));
            });
  }

  @Override
  protected Mono<OVSEventSubscriptionTO> mapSingleD2TO(
      Mono<EventSubscription> eventSubscriptionMono) {
    return eventSubscriptionMono
        .map(eventSubscriptionToEventSubscriptionTo)
        .flatMap(
            eventSubscriptionTO ->
                eventSubscriptionRepository
                    .findEventTypesForSubscription(eventSubscriptionTO.getSubscriptionID())
                    .map(event -> EventType.valueOf(event).name())
                    .collectList()
                    .doOnNext(events -> eventSubscriptionTO.setEventType(String.join(",", events)))
                    .thenReturn(eventSubscriptionTO))
        .flatMap(
            esTo ->
                eventSubscriptionRepository
                    .findTransportEventTypesForSubscriptionID(esTo.getSubscriptionID())
                    .map(se -> TransportEventTypeCode.valueOf(se).name())
                    .collectList()
                    .doOnNext(events -> esTo.setTransportEventTypeCode(String.join(",", events)))
                    .thenReturn(esTo))
        .flatMap(
            esTo ->
                eventSubscriptionRepository
                    .findOperationsEventTypesForSubscriptionID(esTo.getSubscriptionID())
                    .map(se -> OperationsEventTypeCode.valueOf(se).name())
                    .collectList()
                    .doOnNext(events -> esTo.setOperationsEventTypeCode(String.join(",", events)))
                    .thenReturn(esTo));
  }

  // ToDo : replace this with mapstruct
  private final Function<EventSubscription, OVSEventSubscriptionTO>
      eventSubscriptionToEventSubscriptionTo =
          es -> {
            OVSEventSubscriptionTO eventSubscriptionTo = new OVSEventSubscriptionTO();
            eventSubscriptionTo.setSubscriptionID(es.getSubscriptionID());
            eventSubscriptionTo.setCallbackUrl(es.getCallbackUrl());
            eventSubscriptionTo.setCarrierServiceCode(es.getCarrierServiceCode());
            eventSubscriptionTo.setCarrierVoyageNumber(es.getCarrierVoyageNumber());
            eventSubscriptionTo.setVesselIMONumber(es.getVesselIMONumber());
            eventSubscriptionTo.setTransportCallID(es.getTransportCallID());
            return eventSubscriptionTo;
          };
}
