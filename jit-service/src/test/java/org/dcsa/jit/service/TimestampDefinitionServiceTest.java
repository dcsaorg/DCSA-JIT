package org.dcsa.jit.service;

import org.dcsa.jit.persistence.entity.OperationsEvent;
import org.dcsa.jit.persistence.entity.TimestampInfo;
import org.dcsa.jit.persistence.repository.TimestampDefinitionRepository;
import org.dcsa.jit.persistence.repository.TimestampInfoRepository;
import org.dcsa.jit.service.datafactories.TimestampDefinitionDataFactory;
import org.dcsa.skernel.errors.exceptions.ConcreteRequestErrorMessageException;
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
class TimestampDefinitionServiceTest {
  @Mock TimestampDefinitionRepository timestampDefinitionRepository;
  @Mock TimestampInfoRepository timestampInfoRepository;
  @InjectMocks TimestampDefinitionService timestampDefinitionService;

  @Test
  @DisplayName("Test MarkOperationsEventAsTimestamp foe a valid found timestampDefinition.")
  void testMarkOperationsEventAsTimestampWithValidOperationsEventAndTimestamp() {
    when(timestampDefinitionRepository
            .findByEventClassifierCodeAndOperationsEventTypeCodeAndPortCallPhaseTypeCodeAndPortCallServiceTypeCodeAndFacilityTypeCode(
                any(), any(), any(), any(), any()))
        .thenReturn(List.of(TimestampDefinitionDataFactory.timestampDefinition()));

    ArgumentCaptor<TimestampInfo> argumentCaptorTimestampDefinition =
        ArgumentCaptor.forClass(TimestampInfo.class);
    assertDoesNotThrow(
        () -> timestampDefinitionService.markOperationsEventAsTimestamp(new OperationsEvent()));

    verify(timestampInfoRepository, times(1)).save(argumentCaptorTimestampDefinition.capture());

    assertEquals(
        TimestampDefinitionDataFactory.timestampDefinition().getId(),
        argumentCaptorTimestampDefinition.getValue().getTimestampDefinition().getId());
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
        exception
            .getMessage()
            .contains("Cannot determine JIT timestamp type for provided timestamp!"));
  }

  @Test
  @DisplayName(
      "Test MarkOperationsEventAsTimestamp for when no multiple valid timestampDefinitions are found.")
  void testMarkOperationsEventAsTimestampWithMultipleTimestampDefinitions() {
    when(timestampDefinitionRepository
            .findByEventClassifierCodeAndOperationsEventTypeCodeAndPortCallPhaseTypeCodeAndPortCallServiceTypeCodeAndFacilityTypeCode(
                any(), any(), any(), any(), any()))
        .thenReturn(List.of(TimestampDefinitionDataFactory.timestampDefinition(), TimestampDefinitionDataFactory.timestampDefinition()));

    Throwable exception =
        assertThrows(
            ConcreteRequestErrorMessageException.class,
            () -> timestampDefinitionService.markOperationsEventAsTimestamp(new OperationsEvent()));
    assertTrue(
        exception
            .getMessage()
            .contains(
                "There should be exactly one timestamp! More than one JIT timestamp type found for the given fields: "));
  }
}
