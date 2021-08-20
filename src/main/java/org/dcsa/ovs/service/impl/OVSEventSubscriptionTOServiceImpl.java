package org.dcsa.ovs.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.EventSubscription;
import org.dcsa.core.events.model.base.AbstractEventSubscription;
import org.dcsa.core.events.model.enums.EventType;
import org.dcsa.core.events.model.enums.OperationsEventTypeCode;
import org.dcsa.core.events.model.enums.TransportEventTypeCode;
import org.dcsa.core.events.repository.EventSubscriptionRepository;
import org.dcsa.core.events.service.EventSubscriptionService;
import org.dcsa.core.events.service.impl.EventSubscriptionTOServiceImpl;
import org.dcsa.core.exception.UpdateException;
import org.dcsa.core.util.MappingUtils;
import org.dcsa.ovs.model.transferobjects.OVSEventSubscriptionTO;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OVSEventSubscriptionTOServiceImpl
    extends EventSubscriptionTOServiceImpl<OVSEventSubscriptionTO, EventSubscriptionService> {

  private static final List<EventType> ALL_ALLOWED_EVENT_TYPES =
      List.of(EventType.TRANSPORT, EventType.OPERATIONS);

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

    List<EventType> eventTypes;

    if (CollectionUtils.isEmpty(eventSubscriptionTO.getEventType())) {
      eventTypes = ALL_ALLOWED_EVENT_TYPES;
      eventSubscriptionTO.setEventType(eventTypes);
    } else {
      eventTypes = eventSubscriptionTO.getEventType();
    }

    return Flux.fromIterable(eventTypes)
        .concatMap(
            eventType ->
                eventSubscriptionRepository.insertEventTypeForSubscription(
                    eventSubscriptionTO.getSubscriptionID(), eventType))
        .then(Mono.just(eventSubscriptionTO));
  }

  private Mono<Void> createTransportEventType(OVSEventSubscriptionTO eventSubscriptionTO) {
    List<TransportEventTypeCode> transportEventTypeCode =
        eventSubscriptionTO.getTransportEventTypeCode();
    return Flux.fromIterable(transportEventTypeCode)
        .flatMap(
            t ->
                eventSubscriptionRepository.insertTransportEventTypeForSubscription(
                    eventSubscriptionTO.getSubscriptionID(), t))
        .then();
  }

  private Mono<Void> createOperationsEventType(OVSEventSubscriptionTO eventSubscriptionTO) {
    List<OperationsEventTypeCode> operationsEventTypeCode =
        eventSubscriptionTO.getOperationsEventTypeCode();
    return Flux.fromIterable(operationsEventTypeCode)
        .flatMap(
            o ->
                eventSubscriptionRepository.insertOperationsEventTypeForSubscription(
                    eventSubscriptionTO.getSubscriptionID(), o))
        .then();
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
                        if (CollectionUtils.isEmpty(subscriptionTO.getEventType())) {
                          subscriptionTO.setEventType(new ArrayList<>());
                        }
                        subscriptionTO
                            .getEventType()
                            .add(eventSubscriptionEventType.getEventType());
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
                        if (CollectionUtils.isEmpty(subscriptionTO.getTransportEventTypeCode())) {
                          subscriptionTO.setTransportEventTypeCode(new ArrayList<>());
                        }
                        subscriptionTO
                            .getTransportEventTypeCode()
                            .add(esTransportEventType.getTransportEventTypeCode());
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
                        if (CollectionUtils.isEmpty(subscriptionTO.getOperationsEventTypeCode())) {
                          subscriptionTO.setOperationsEventTypeCode(new ArrayList<>());
                        }
                        subscriptionTO
                            .getOperationsEventTypeCode()
                            .add(esOperationsEventType.getOperationsEventTypeCode());
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
                    .map(EventType::valueOf)
                    .collectList()
                    .doOnNext(eventSubscriptionTO::setEventType)
                    .thenReturn(eventSubscriptionTO))
        .flatMap(
            esTo ->
                eventSubscriptionRepository
                    .findTransportEventTypesForSubscriptionID(esTo.getSubscriptionID())
                    .map(TransportEventTypeCode::valueOf)
                    .collectList()
                    .doOnNext(esTo::setTransportEventTypeCode)
                    .thenReturn(esTo))
        .flatMap(
            esTo ->
                eventSubscriptionRepository
                    .findOperationsEventTypesForSubscriptionID(esTo.getSubscriptionID())
                    .map(OperationsEventTypeCode::valueOf)
                    .collectList()
                    .doOnNext(esTo::setOperationsEventTypeCode)
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
