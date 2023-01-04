package org.dcsa.jit.persistence.repository.specification;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.dcsa.jit.persistence.entity.*;
import org.dcsa.jit.persistence.entity.enums.EventClassifierCode;
import org.dcsa.jit.persistence.entity.enums.OperationsEventTypeCode;
import org.dcsa.skernel.domain.persistence.entity.Facility;
import org.dcsa.skernel.domain.persistence.entity.Facility_;
import org.dcsa.skernel.domain.persistence.entity.Location;
import org.dcsa.skernel.infrastructure.http.queryparams.ParsedQueryParameter;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OperationsEventSpecification {

  @Builder
  public static class OperationsEventFilters {
    String transportCallID;
    String vesselIMONumber;
    String carrierVoyageNumber;
    String exportVoyageNumber;
    String carrierExportVoyageNumber;
    String carrierServiceCode;
    String unLocationCode;
    String facilitySMDGCode;
    List<OperationsEventTypeCode> operationsEventTypeCodes;
    List<EventClassifierCode> eventClassifierCodes;
    List<ParsedQueryParameter<OffsetDateTime>> eventCreatedDateTime;
  }

  public static Specification<OperationsEvent> withFilters(final OperationsEventFilters filters) {
    return (root, query, builder) -> {
      Join<OperationsEvent, TransportCall> operationsEventTransportCallJoin = root.join("transportCall", JoinType.LEFT);
      Join<TransportCall, Voyage> transportCallExportVoyageJoin = operationsEventTransportCallJoin.join("exportVoyage", JoinType.LEFT);
      Join<Voyage, Service> voyageServiceJoin = transportCallExportVoyageJoin.join("service", JoinType.LEFT);
      Join<TransportCall, Vessel> transportCallVesselJoin = operationsEventTransportCallJoin.join("vessel", JoinType.LEFT);
      Join<TransportCall, Location> transportCallLocationJoin = operationsEventTransportCallJoin.join("location", JoinType.LEFT);
      Join<Location, Facility> locationFacilityJoin = transportCallLocationJoin.join("facility", JoinType.LEFT);

      List<Predicate> predicates = new ArrayList<>();

      if (null != filters.transportCallID) {
        Predicate predicate = builder.equal(operationsEventTransportCallJoin.get("transportCallReference"), filters.transportCallID);
        predicates.add(predicate);
      }

      if (null != filters.vesselIMONumber) {
        Predicate predicate = builder.equal(transportCallVesselJoin.get("vesselIMONumber"), filters.vesselIMONumber);
        predicates.add(predicate);
      }

      if (null != filters.carrierVoyageNumber) {
        Predicate predicate = builder.equal(transportCallExportVoyageJoin.get(Voyage_.CARRIER_VOYAGE_NUMBER), filters.carrierVoyageNumber);
        predicates.add(predicate);
      }

      if (null != filters.exportVoyageNumber) {
        Predicate predicate = builder.equal(transportCallExportVoyageJoin.get(Voyage_.CARRIER_VOYAGE_NUMBER), filters.exportVoyageNumber);
        predicates.add(predicate);
      }

      if (null != filters.carrierExportVoyageNumber) {
        Predicate predicate = builder.equal(transportCallExportVoyageJoin.get(Voyage_.CARRIER_VOYAGE_NUMBER), filters.carrierExportVoyageNumber);
        predicates.add(predicate);
      }

      if (null != filters.carrierServiceCode) {
        Predicate predicate = builder.equal(voyageServiceJoin.get(Service_.CARRIER_SERVICE_CODE), filters.carrierServiceCode);
        predicates.add(predicate);
      }

      if (null != filters.unLocationCode) {
        Predicate predicate = builder.equal(transportCallLocationJoin.get("UNLocationCode"), filters.unLocationCode);
        predicates.add(predicate);
      }

      if (null != filters.facilitySMDGCode) {
        Predicate predicate;
        if (filters.facilitySMDGCode.equalsIgnoreCase("null")) {
          predicate = builder.isNull(locationFacilityJoin.get(Facility_.FACILITY_SM_DG_CODE));
        } else {
          predicate = builder.equal(locationFacilityJoin.get(Facility_.FACILITY_SM_DG_CODE), filters.facilitySMDGCode);
        }
        predicates.add(predicate);
      }

      if (null != filters.operationsEventTypeCodes && !filters.operationsEventTypeCodes.isEmpty()) {
        Predicate predicate = root.get(OperationsEvent_.OPERATIONS_EVENT_TYPE_CODE).in(filters.operationsEventTypeCodes);
        predicates.add(predicate);
      }

      if (null != filters.eventClassifierCodes && !filters.eventClassifierCodes.isEmpty()) {
        Predicate predicate = root.get(OperationsEvent_.EVENT_CLASSIFIER_CODE).in(filters.eventClassifierCodes);
        predicates.add(predicate);
      }

      if (null != filters.eventCreatedDateTime && !filters.eventCreatedDateTime.isEmpty()) {
        for (ParsedQueryParameter<OffsetDateTime> param : filters.eventCreatedDateTime) {
          Predicate predicate = switch (param.comparisonType()) {
            case EQ -> builder.equal(root.get("eventCreatedDateTime"), param.value());
            case LT -> builder.lessThan(root.get("eventCreatedDateTime"), param.value());
            case LTE -> builder.lessThanOrEqualTo(root.get("eventCreatedDateTime"), param.value());
            case GT -> builder.greaterThan(root.get("eventCreatedDateTime"), param.value());
            case GTE -> builder.greaterThanOrEqualTo(root.get("eventCreatedDateTime"), param.value());
          };
          predicates.add(predicate);
        }
      }

      return builder.and(predicates.toArray(Predicate[]::new));
    };
  }
}
