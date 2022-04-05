package org.dcsa.jit.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.EventSubscription;
import org.dcsa.core.events.model.base.AbstractEventSubscription;
import org.dcsa.core.events.model.enums.*;
import org.dcsa.core.events.repository.EventSubscriptionRepository;
import org.dcsa.core.events.service.EventSubscriptionService;
import org.dcsa.core.events.service.impl.EventSubscriptionTOServiceImpl;
import org.dcsa.core.util.MappingUtils;
import org.dcsa.jit.model.transferobjects.JITEventSubscriptionTO;
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
public class JITEventSubscriptionTOServiceImpl
    extends EventSubscriptionTOServiceImpl<
        JITEventSubscriptionTO, EventSubscriptionService, EventSubscriptionRepository> {

  private final EventSubscriptionService eventSubscriptionService;
  private final EventSubscriptionRepository eventSubscriptionRepository;

  @Override
  protected EventSubscriptionService getService() {
    return this.eventSubscriptionService;
  }

  @Override
  protected EventSubscriptionRepository getRepository() {
    return this.eventSubscriptionRepository;
  }

  @Override
   public List<EventType> getAllowedEventTypes() {
    return List.of(EventType.TRANSPORT, EventType.OPERATIONS);
  }

  @Override
  protected Mono<EventSubscription> findShallowEventSubscriptionById(UUID id) {
    return eventSubscriptionRepository.findById(id);
  }

  @Override
  public Flux<JITEventSubscriptionTO> findAll() {
    return mapManyD2TO(eventSubscriptionRepository.findAll());
  }

  // ToDo : replace this with mapstruct
  @Override
  protected Function<JITEventSubscriptionTO, EventSubscription>
      eventSubscriptionTOToEventSubscription() {
    return esTo -> {
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
  }

  // ToDo : replace this with mapstruct
  protected Function<EventSubscription, JITEventSubscriptionTO>
      eventSubscriptionToEventSubscriptionTo() {
    return es -> {
      JITEventSubscriptionTO eventSubscriptionTo = new JITEventSubscriptionTO();
      eventSubscriptionTo.setSubscriptionID(es.getSubscriptionID());
      eventSubscriptionTo.setCallbackUrl(es.getCallbackUrl());
      eventSubscriptionTo.setCarrierServiceCode(es.getCarrierServiceCode());
      eventSubscriptionTo.setCarrierVoyageNumber(es.getCarrierVoyageNumber());
      eventSubscriptionTo.setVesselIMONumber(es.getVesselIMONumber());
      eventSubscriptionTo.setTransportCallID(es.getTransportCallID());
      return eventSubscriptionTo;
    };
  }

  @Override
  protected List<EventType> getEventTypesForTo(JITEventSubscriptionTO eventSubscriptionTO) {

    if (null == eventSubscriptionTO.getEventType()
        || eventSubscriptionTO.getEventType().isEmpty()) {
      eventSubscriptionTO.setEventType(getAllowedEventTypes());
    }

    return eventSubscriptionTO.getEventType();
  }

  @Override
  protected List<TransportDocumentTypeCode> getTransportDocumentTypesForTo(
      JITEventSubscriptionTO eventSubscriptionTO) {

    // we don't need TransportDocumentTypeCode for JIT event subscriptions
    throw new UnsupportedOperationException();
  }

  @Override
  protected List<ShipmentEventTypeCode> getShipmentEventTypeCodesForTo(
      JITEventSubscriptionTO eventSubscriptionTO) {

    // we don't need ShipmentEventTypeCode for JIT event subscriptions
    throw new UnsupportedOperationException();
  }

  @Override
  protected List<TransportEventTypeCode> getTransportEventTypeCodesForTo(
      JITEventSubscriptionTO eventSubscriptionTO) {

    if (null == eventSubscriptionTO.getTransportEventTypeCode()
        || eventSubscriptionTO.getTransportEventTypeCode().isEmpty()) {
      eventSubscriptionTO.setTransportEventTypeCode(ALL_TRANSPORT_EVENT_TYPES);
    }

    return eventSubscriptionTO.getTransportEventTypeCode();
  }

  @Override
  protected List<EquipmentEventTypeCode> getEquipmentEventTypeCodesForTo(
      JITEventSubscriptionTO eventSubscriptionTO) {

    // we don't need EquipmentEventTypeCode for JIT event subscriptions
    throw new UnsupportedOperationException();
  }

  @Override
  protected List<OperationsEventTypeCode> getOperationsEventTypeCodesForTo(
      JITEventSubscriptionTO eventSubscriptionTO) {

    if (null == eventSubscriptionTO.getOperationsEventTypeCode()
        || eventSubscriptionTO.getOperationsEventTypeCode().isEmpty()) {
      eventSubscriptionTO.setOperationsEventTypeCode(ALL_OPERATIONS_EVENT_TYPES);
    }

    return eventSubscriptionTO.getOperationsEventTypeCode();
  }

  @Override
  public Mono<JITEventSubscriptionTO> create(JITEventSubscriptionTO eventSubscriptionTO) {
    return validateCreateRequest(eventSubscriptionTO)
        .then(
            eventSubscriptionService.create(
                eventSubscriptionTOToEventSubscription().apply(eventSubscriptionTO)))
        .flatMap(
            eventSubscription -> {
              eventSubscriptionTO.setSubscriptionID(eventSubscription.getSubscriptionID());
              return createEventTypes(eventSubscriptionTO)
                  .then(createTransportEventType(eventSubscriptionTO))
                  .then(createOperationsEventType(eventSubscriptionTO))
                  .thenReturn(eventSubscriptionTO);
            });
  }

  @Override
  public Mono<JITEventSubscriptionTO> update(JITEventSubscriptionTO eventSubscriptionTO) {
    return validateUpdateRequest(eventSubscriptionTO)
        .then(
            eventSubscriptionRepository.deleteEventTypesForSubscription(
                eventSubscriptionTO.getSubscriptionID()))
        .thenReturn(eventSubscriptionTO)
        .map(eventSubscriptionTOToEventSubscription())
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
  protected Flux<JITEventSubscriptionTO> mapManyD2TO(
      Flux<EventSubscription> eventSubscriptionFlux) {
    return eventSubscriptionFlux
        .map(eventSubscriptionToEventSubscriptionTo())
        .collectList()
        .flatMapMany(
            eventSubscriptionList -> {
              Map<UUID, JITEventSubscriptionTO> id2subscription =
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
                        JITEventSubscriptionTO subscriptionTO =
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
                        JITEventSubscriptionTO subscriptionTO =
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
                        JITEventSubscriptionTO subscriptionTO =
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
  protected Mono<JITEventSubscriptionTO> mapSingleD2TO(
      Mono<EventSubscription> eventSubscriptionMono) {
    return eventSubscriptionMono
        .map(eventSubscriptionToEventSubscriptionTo())
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

}
