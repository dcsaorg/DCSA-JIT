package org.dcsa.jit.controller;

import org.dcsa.jit.persistence.repository.*;
import org.dcsa.jit.service.OperationsEventService;
import org.dcsa.jit.service.TimestampDefinitionService;
import org.dcsa.skernel.infrastructure.pagination.PagedResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Test for operations event endpoint")
@WebMvcTest(controllers = {EventController.class})
class OperationsEventControllerTest {

  @Autowired MockMvc mockMvc;

  @MockBean OperationsEventService operationsEventService;
  @MockBean TimestampDefinitionService timestampDefinitionService;

  @MockBean TimestampDefinitionRepository timestampDefinitionRepository;
  @MockBean TransportCallRepository transportCallRepository;
  @MockBean AddressRepository addressRepository;
  @MockBean PartyRepository partyRepository;
  @MockBean LocationRepository locationRepository;
  @MockBean FacilityRepository facilityRepository;
  @MockBean VesselRepository vesselRepository;
  @MockBean ServiceRepository serviceRepository;
  @MockBean OperationsEventRepository operationsEventRepository;
  @MockBean UnLocationRepository unLocationRepository;
  @MockBean UnmappedEventRepository unmappedEventRepository;

  @Test
  @DisplayName("GET operations event should return 200 for given basic valid call")
  void testGetOperationsEventReturns200ForGivenBasicCall() throws Exception {
    when(operationsEventService.findAll(any(), any()))
        .thenReturn(
            new PagedResult<>(0, Collections.emptyList()));
    this.mockMvc
        .perform(get("/events").accept(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString()
        .equals("[]");
  }

  @Test
  @DisplayName(
      "GET operations event should return 400 for invalid carrierServiceCode request param length")
  void testGetServiceSchedulerReturns400ForInvalidTransportCallIdLength() throws Exception {
    this.mockMvc
        .perform(
            get("/events")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .param("transportCallID", "x".repeat(101)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.httpMethod").value("GET"))
        .andExpect(jsonPath("$.requestUri").value("/events"))
        .andExpect(jsonPath("$.errors[0].reason").value("invalidInput"))
        .andExpect(
            jsonPath("$.errors[0].message")
                .value(containsString("size must be between 0 and 100")));
  }

  @Test
  @DisplayName(
      "GET operations event should return 400 for invalid vesselIMONumber request param length")
  void testGetServiceSchedulerReturns400ForInvalidVesselIMONumberLength() throws Exception {
    this.mockMvc
        .perform(
            get("/events")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .param("vesselIMONumber", "1234566"))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.httpMethod").value("GET"))
        .andExpect(jsonPath("$.requestUri").value("/events"))
        .andExpect(jsonPath("$.errors[0].reason").value("invalidInput"))
        .andExpect(
            jsonPath("$.errors[0].message")
                .value(containsString("must be a valid Vessel IMO Number")));
  }

  @Test
  @DisplayName(
      "GET operations event should return 400 for invalid voyageNumber request param length")
  void testGetServiceSchedulerReturns400ForCarrierVoyageNumberLength() throws Exception {

    this.mockMvc
        .perform(
            get("/events")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .param("carrierVoyageNumber", "x".repeat(51)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.httpMethod").value("GET"))
        .andExpect(jsonPath("$.requestUri").value("/events"))
        .andExpect(jsonPath("$.errors[0].reason").value("invalidInput"))
        .andExpect(
            jsonPath("$.errors[0].message").value(containsString("size must be between 0 and 50")));
  }

  @Test
  @DisplayName(
      "GET operations event should return 400 for invalid universalServiceReference request param length")
  void testGetServiceSchedulerReturns400ForInvalidExportVoyageNumberLength() throws Exception {
    this.mockMvc
        .perform(
            get("/events")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .param("exportVoyageNumber", "x".repeat(51)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.httpMethod").value("GET"))
        .andExpect(jsonPath("$.requestUri").value("/events"))
        .andExpect(jsonPath("$.errors[0].reason").value("invalidInput"))
        .andExpect(
            jsonPath("$.errors[0].message").value(containsString("size must be between 0 and 50")));
  }

  @Test
  @DisplayName(
      "GET operations event should return 400 for invalid carrierServiceCode request param length")
  void testGetServiceSchedulerReturns400ForInvalidCarrierServiceCodeLength() throws Exception {
    this.mockMvc
        .perform(
            get("/events")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .param("carrierServiceCode", "x".repeat(6)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.httpMethod").value("GET"))
        .andExpect(jsonPath("$.requestUri").value("/events"))
        .andExpect(jsonPath("$.errors[0].reason").value("invalidInput"))
        .andExpect(
            jsonPath("$.errors[0].message").value(containsString("size must be between 0 and 5")));
  }

  @Test
  @DisplayName(
      "GET operations event should return 400 for invalid UNLocationCode request param length")
  void testGetServiceSchedulerReturns400ForUNLocationCodeLength() throws Exception {

    this.mockMvc
        .perform(
            get("/events")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .param("UNLocationCode", "x".repeat(6)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.httpMethod").value("GET"))
        .andExpect(jsonPath("$.requestUri").value("/events"))
        .andExpect(jsonPath("$.errors[0].reason").value("invalidInput"))
        .andExpect(
            jsonPath("$.errors[0].message").value(containsString("size must be between 0 and 5")));
  }

  @Test
  @DisplayName(
      "GET operations event should return 400 for invalid facilitySMDGCode request param length")
  void testGetServiceSchedulerReturns400ForFacilitySMDGCodeLength() throws Exception {

    this.mockMvc
        .perform(
            get("/events")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .param("facilitySMDGCode", "x".repeat(6)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.httpMethod").value("GET"))
        .andExpect(jsonPath("$.requestUri").value("/events"))
        .andExpect(jsonPath("$.errors[0].reason").value("invalidInput"))
        .andExpect(
            jsonPath("$.errors[0].message").value(containsString("size must be between 0 and 5")));
  }

  @Test
  @DisplayName("GET operations event should return 400 for invalid vesselName request param length")
  void testGetServiceSchedulerReturns400ForOperationsEventTypeCodeLength() throws Exception {

    this.mockMvc
        .perform(
            get("/events")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .param("operationsEventTypeCode", "x".repeat(6)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.httpMethod").value("GET"))
        .andExpect(jsonPath("$.requestUri").value("/events"))
        .andExpect(jsonPath("$.errors[0].reason").value("invalidInput"))
        .andExpect(
            jsonPath("$.errors[0].message").value(containsString("size must be between 0 and 5")));
  }
}
