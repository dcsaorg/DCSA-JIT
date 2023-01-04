package org.dcsa.jit.service;

import lombok.RequiredArgsConstructor;
import org.dcsa.jit.persistence.entity.Vessel;
import org.dcsa.jit.persistence.repository.VesselRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class VesselService {
  private final VesselRepository vesselRepository;

  @Transactional
  public Vessel ensureVesselExistsByImoNumber(String imoNumber) {
    return ensureVesselExistsByImoNumber(imoNumber, false);
  }

  @Transactional
  public Vessel ensureVesselExistsByImoNumber(String imoNumber, boolean dummy) {
    return vesselRepository.findByVesselIMONumber(imoNumber)
      .orElseGet(() -> vesselRepository.save(
        Vessel.builder()
          .vesselIMONumber(imoNumber)
          .isDummy(dummy)
          .build()
      ));
  }
}
