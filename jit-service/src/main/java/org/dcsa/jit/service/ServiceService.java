package org.dcsa.jit.service;

import lombok.RequiredArgsConstructor;
import org.dcsa.jit.persistence.repository.ServiceRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceService {
  private final ServiceRepository serviceRepository;

  @Transactional
  public org.dcsa.jit.persistence.entity.Service ensureServiceExistsByCarrierServiceCode(String carrierServiceCode) {
    List<org.dcsa.jit.persistence.entity.Service> services = serviceRepository.findByCarrierServiceCode(carrierServiceCode);
    if (!services.isEmpty()) {
      return services.get(0);
    }
    return serviceRepository.save(org.dcsa.jit.persistence.entity.Service.builder()
      .carrierServiceCode(carrierServiceCode)
      .build());
  }
}
