package org.dcsa.jit.service;

import org.dcsa.jit.mapping.EnumMappers;
import org.dcsa.jit.persistence.repository.TimestampDefinitionRepository;
import org.dcsa.jit.service.datafactories.TimestampDefinitionDataFactory;
import org.dcsa.jit.transferobjects.TimestampTO;
import org.dcsa.skernel.errors.exceptions.ConcreteRequestErrorMessageException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TimestampDefinitionServiceTest {

  @Mock EnumMappers enumMappers;
  @Mock TimestampDefinitionRepository timestampDefinitionRepository;
  @InjectMocks TimestampDefinitionService timestampDefinitionService;

  @Test
  @DisplayName("Test MarkOperationsEventAsTimestamp foe a valid found timestampDefinition.")
  void testMarkOperationsEventAsTimestampWithValidOperationsEventAndTimestamp() {
    when(timestampDefinitionRepository
            .findByEventClassifierCodeAndOperationsEventTypeCodeAndPortCallPhaseTypeCodeAndPortCallServiceTypeCodeAndFacilityTypeCode(
                any(), any(), any(), any(), any()))
        .thenReturn(List.of(TimestampDefinitionDataFactory.timestampDefinition()));

    var timestamp = TimestampTO.builder().build();
    var tsDef = assertDoesNotThrow(
        () -> timestampDefinitionService.findTimestampDefinition(timestamp));

    assertEquals(
        TimestampDefinitionDataFactory.timestampDefinition().getId(),
        tsDef.getId());
  }

  @Test
  @DisplayName("Test MarkOperationsEventAsTimestamp for when no valid timestampDefinition found.")
  void testMarkOperationsEventAsTimestampWithNoValidTimestampDefinition() {
    when(timestampDefinitionRepository
            .findByEventClassifierCodeAndOperationsEventTypeCodeAndPortCallPhaseTypeCodeAndPortCallServiceTypeCodeAndFacilityTypeCode(
                any(), any(), any(), any(), any()))
        .thenReturn(Collections.emptyList());
    var timestamp = TimestampTO.builder().build();

    Throwable exception =
        assertThrows(
            ConcreteRequestErrorMessageException.class,
            () -> timestampDefinitionService.findTimestampDefinition(timestamp));
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
    var timestamp = TimestampTO.builder().build();
    Throwable exception =
        assertThrows(
            ConcreteRequestErrorMessageException.class,
            () -> timestampDefinitionService.findTimestampDefinition(timestamp));
    assertTrue(
        exception
            .getMessage()
            .contains(
                "There should be exactly one timestamp! More than one JIT timestamp type found for the given fields: "));
  }
}
