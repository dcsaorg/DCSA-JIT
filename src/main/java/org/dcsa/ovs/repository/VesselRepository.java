package org.dcsa.ovs.repository;

import org.dcsa.core.repository.ExtendedRepository;
import org.dcsa.core.repository.InsertAddonRepository;
import org.dcsa.ovs.model.Vessel;

public interface VesselRepository extends ExtendedRepository<Vessel, String>, InsertAddonRepository<Vessel> {
}
