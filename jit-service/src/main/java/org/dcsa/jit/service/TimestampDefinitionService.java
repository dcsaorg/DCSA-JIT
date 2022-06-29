package org.dcsa.jit.service;

import lombok.RequiredArgsConstructor;
import org.dcsa.jit.persistence.entity.OperationsEvent;
import org.dcsa.jit.persistence.entity.OpsEventTimestampDefinition;
import org.dcsa.jit.persistence.entity.TimestampDefinition;
import org.dcsa.jit.persistence.entity.enums.OperationsEventTypeCode;
import org.dcsa.jit.persistence.entity.enums.PortCallPhaseTypeCode;
import org.dcsa.jit.persistence.entity.enums.PortCallServiceTypeCode;
import org.dcsa.jit.persistence.entity.enums.PublisherRole;
import org.dcsa.jit.persistence.repository.OpsEventTimestampDefinitionRepository;
import org.dcsa.jit.persistence.repository.TimestampDefinitionRepository;
import org.dcsa.skernel.errors.exceptions.ConcreteRequestErrorMessageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
                operationsEvent.getEventClassifierCode(),
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
            .eventID(operationsEvent.getEventID())
            .operationsEvent(operationsEvent)
            .timestampDefinition(timestampDefinitionList.get(0))
            .newRecord(true)
            .build();
    opsEventTimestampDefinitionRepository.save(ops);
  }

  public PortCallPhaseTypeCode findPhaseTypeCodeFromOperationsEventForJit1_0(
      OperationsEvent operationsEvent) {

    List<TimestampDefinition> jit1_0 =
        timestampDefinitionRepository
            .findByEventClassifierCodeAndOperationsEventTypeCodeAndProvidedInStandardAndPortCallServiceTypeCodeAndFacilityTypeCode(
                operationsEvent.getEventClassifierCode(),
                operationsEvent.getOperationsEventTypeCode(),
                "jit1_0",
                operationsEvent.getPortCallServiceTypeCode(),
                operationsEvent.getFacilityTypeCode());

    // PortCallPhaseTypeCode not part of JIT 1.0
    if (jit1_0.isEmpty()) {
      return null;
    }

    if (jit1_0.size() > 1) {
      String errorMessage =
          "EventClassifierCode: "
              + operationsEvent.getEventClassifierCode()
              + ", OperationsEventTypeCode: "
              + operationsEvent.getOperationsEventTypeCode()
              + ", PortCallServiceTypeCode: "
              + operationsEvent.getPortCallServiceTypeCode()
              + ", FacilityTypeCode: "
              + operationsEvent.getFacilityTypeCode();

      throw ConcreteRequestErrorMessageException.internalServerError(
          "More than one JIT 1.0 timestamp definitions found according to the given fields: "
              + errorMessage);
    }

    return jit1_0.stream().findFirst().get().getPortCallPhaseTypeCode();
  }

  public PortCallPhaseTypeCode findOmittedPhaseTypeCodeFromOperationsEventForJit1_1(
      OperationsEvent operationsEvent) {

    // JIT 1.1 use-cases with omitted port call phase type code
    List<String> phaseTypeCodeOmitted = IntStream.range(5, 13).boxed().map(i -> "UC" + i).toList();
    List<TimestampDefinition> timestampDefinitionList =
        timestampDefinitionRepository.findAllById(phaseTypeCodeOmitted);
    Stream<TimestampDefinition> match =
        timestampDefinitionList.stream()
            .filter(
                x ->
                    x.getEventClassifierCode() == operationsEvent.getEventClassifierCode()
                        && x.getOperationsEventTypeCode()
                            == operationsEvent.getOperationsEventTypeCode()
                        && x.getFacilityTypeCode() == operationsEvent.getFacilityTypeCode());
    if (match.findFirst().isPresent()) {
      return match.findFirst().get().getPortCallPhaseTypeCode();
    }
    return null;
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
