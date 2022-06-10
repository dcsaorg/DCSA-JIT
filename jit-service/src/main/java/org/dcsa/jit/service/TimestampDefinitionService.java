package org.dcsa.jit.service;

import lombok.RequiredArgsConstructor;
import org.dcsa.jit.persistence.entity.OperationsEvent;
import org.dcsa.jit.persistence.entity.OpsEventTimestampDefinition;
import org.dcsa.jit.persistence.entity.TimestampDefinition;
import org.dcsa.jit.persistence.entity.enums.OperationsEventTypeCode;
import org.dcsa.jit.persistence.entity.enums.PortCallServiceTypeCode;
import org.dcsa.jit.persistence.entity.enums.PublisherRole;
import org.dcsa.jit.persistence.repository.OpsEventTimestampDefinitionRepository;
import org.dcsa.jit.persistence.repository.TimestampDefinitionRepository;
import org.dcsa.skernel.errors.exceptions.ConcreteRequestErrorMessageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TimestampDefinitionService {

  /** Helper for {@link #arePublisherRolesInterchangeable(PublisherRole, PublisherRole)} */
  private static final Map<PublisherRole, PublisherRole> NORMALIZED_PARTY_FUNCTION_MAP =
      Map.of(
          PublisherRole.AG, PublisherRole.CA,
          PublisherRole.VSL, PublisherRole.CA);

  private final TimestampDefinitionRepository timestampDefinitionRepository;
  private final OpsEventTimestampDefinitionRepository opsEventTimestampDefinitionRepository;

  @Transactional
  public void markOperationsEventAsTimestamp(OperationsEvent operationsEvent) {
    List<TimestampDefinition> timestampDefinitionList =
        timestampDefinitionRepository
            .findByEventClassifierCodeAndOperationsEventTypeCodeAndPortCallPhaseTypeCodeAndPortCallServiceTypeCodeAndFacilityTypeCode(
                operationsEvent.getClassifierCode(),
                operationsEvent.getOperationsEventTypeCode(),
                operationsEvent.getPortCallPhaseTypeCode(),
                operationsEvent.getPortCallServiceTypeCode(),
                operationsEvent.getFacilityTypeCode())
            .stream()
            .filter(definition -> isCorrectTimestampsForEvent(definition, operationsEvent))
            .toList();

    if (timestampDefinitionList.isEmpty()) {
      throw ConcreteRequestErrorMessageException.invalidInput(
          "Cannot determine timestamp type for provided timestamp - please verify publisherRole, eventClassifierCode, facilityTypeCode, portCallPhaseTypeCode, and portCallServiceTypeCode");
    }
    if (timestampDefinitionList.size() >= 2) {
      throw ConcreteRequestErrorMessageException.internalServerError(
          "There should exactly one timestamp matching this input but we got two!");
    }
    OpsEventTimestampDefinition ops =
        OpsEventTimestampDefinition.builder()
            .eventID(operationsEvent.getId())
            .timestampDefinitionID(timestampDefinitionList.get(0).getId())
            .build();
    opsEventTimestampDefinitionRepository.save(ops);
  }

  /**
   * Detect mismatching timestamp definitions
   *
   * <p>Ideally, all timestamp definitions would be distinct without resorting to these tricks.
   * Unfortunately, we have "Terminal ready for vessel departure" and "Vessel ready to sail", which
   * can basically only be told apart based on publisher role (or mode of transport, but we have
   * normalized that as OperationEvents require that field to be "not null").
   *
   * <p>This Predicate-like method is here to prune obvious mismatches
   */
  private boolean isCorrectTimestampsForEvent(
      TimestampDefinition definition, OperationsEvent operationsEvent) {
    // Since it is gross hack to rely on publisherRole, lets limit it to only the JIT 1.1 version of
    // these problematic timestamps
    // - they are the only ones that are "SAFE" + "DEPA"
    if (operationsEvent.getPortCallServiceTypeCode() == PortCallServiceTypeCode.SAFE
        && operationsEvent.getOperationsEventTypeCode() == OperationsEventTypeCode.DEPA) {
      return arePublisherRolesInterchangeable(
          definition.getPublisherRole(), operationsEvent.getPublisherRole());
    }
    return true;
  }

  /**
   * Determine if two party functions (publisherRoles) are interchangeable
   *
   * <p>In JIT, most of the carrier published timestamps can freely choose one of the CA, AG or VSL
   * publisherRole even though the IFS denotes a concrete one as the example.
   *
   * @return true if the two party functions are either identical or both of them are one of CA, AG
   *     or VSL.
   */
  private static boolean arePublisherRolesInterchangeable(PublisherRole lhs, PublisherRole rhs) {
    PublisherRole lhsNormalized = NORMALIZED_PARTY_FUNCTION_MAP.getOrDefault(lhs, lhs);
    PublisherRole rhsNormalized = NORMALIZED_PARTY_FUNCTION_MAP.getOrDefault(rhs, rhs);
    return lhsNormalized == rhsNormalized;
  }
}
