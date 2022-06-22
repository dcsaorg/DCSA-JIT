package org.dcsa.jit.service;

import org.dcsa.jit.persistence.entity.OperationsEvent;
import org.dcsa.jit.persistence.entity.OpsEventTimestampDefinition;
import org.dcsa.jit.persistence.entity.TimestampDefinition;
import org.dcsa.jit.persistence.entity.enums.EventClassifierCode;
import org.dcsa.jit.persistence.entity.enums.FacilityTypeCodeOPR;
import org.dcsa.jit.persistence.entity.enums.OperationsEventTypeCode;
import org.dcsa.jit.persistence.repository.OpsEventTimestampDefinitionRepository;
import org.dcsa.jit.persistence.repository.TimestampDefinitionRepository;
import org.dcsa.skernel.errors.exceptions.ConcreteRequestErrorMessageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TimestampDefinitionServiceTest {
  @Mock TimestampDefinitionRepository timestampDefinitionRepository;
  @Mock
  OpsEventTimestampDefinitionRepository opsEventTimestampDefinitionRepository;
  @InjectMocks TimestampDefinitionService timestampDefinitionService;
  TimestampDefinition timestampDefinition;

  @BeforeEach
  void init() {
    initEntities();
  }

  private void initEntities() {

    timestampDefinition =
        TimestampDefinition.builder()
            .id("randomID")
            .eventClassifierCode(EventClassifierCode.ACT)
            .facilityTypeCode(FacilityTypeCodeOPR.BRTH)
            .isTerminalNeeded(false)
            .acceptTimestampDefinition(String.valueOf(true))
            .isBerthLocationNeeded(false)
            .isPBPLocationNeeded(false)
            .isVesselPositionNeeded(false)
            .negotiationCycle(String.valueOf(1))
            .operationsEventTypeCode(OperationsEventTypeCode.ARRI)
            .build();
  }

  @Test
  @DisplayName("Test MarkOperationsEventAsTimestamp foe a valid found timestampDefinition.")
  void testMarkOperationsEventAsTimestampWithValidOperationsEventAndTimestamp() {
    when(timestampDefinitionRepository
            .findByEventClassifierCodeAndOperationsEventTypeCodeAndPortCallPhaseTypeCodeAndPortCallServiceTypeCodeAndFacilityTypeCode(
                any(), any(), any(), any(), any()))
        .thenReturn(List.of(timestampDefinition));

    ArgumentCaptor<OpsEventTimestampDefinition> argumentCaptorTimestampDefinition =
        ArgumentCaptor.forClass(OpsEventTimestampDefinition.class);
    assertDoesNotThrow(
        () -> timestampDefinitionService.markOperationsEventAsTimestamp(new OperationsEvent()));

    verify(opsEventTimestampDefinitionRepository, times(1))
      .save(argumentCaptorTimestampDefinition.capture());

    assertEquals(timestampDefinition.getId(), argumentCaptorTimestampDefinition.getValue().getTimestampDefinition().getId());
  }

  @Test
  @DisplayName("Test MarkOperationsEventAsTimestamp for when no valid timestampDefinition found.")
  void testMarkOperationsEventAsTimestampWithNoValidTimestampDefinition() {
    when(timestampDefinitionRepository
            .findByEventClassifierCodeAndOperationsEventTypeCodeAndPortCallPhaseTypeCodeAndPortCallServiceTypeCodeAndFacilityTypeCode(
                any(), any(), any(), any(), any()))
        .thenReturn(Collections.emptyList());

    Throwable exception =
        assertThrows(
            ConcreteRequestErrorMessageException.class,
            () -> timestampDefinitionService.markOperationsEventAsTimestamp(new OperationsEvent()));
    assertTrue(
        exception.getMessage().contains("Cannot determine timestamp type for provided timestamp"));
  }

  @Test
  @DisplayName(
      "Test MarkOperationsEventAsTimestamp for when no multiple valid timestampDefinitions are found.")
  void testMarkOperationsEventAsTimestampWithMultipleTimestampDefinitions() {
    when(timestampDefinitionRepository
            .findByEventClassifierCodeAndOperationsEventTypeCodeAndPortCallPhaseTypeCodeAndPortCallServiceTypeCodeAndFacilityTypeCode(
                any(), any(), any(), any(), any()))
        .thenReturn(List.of(timestampDefinition, timestampDefinition));

    Throwable exception =
        assertThrows(
            ConcreteRequestErrorMessageException.class,
            () -> timestampDefinitionService.markOperationsEventAsTimestamp(new OperationsEvent()));
    assertTrue(
        exception
            .getMessage()
            .contains("There should exactly one timestamp matching this input but we got two"));
  }
}
