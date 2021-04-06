-- Assumes the PSQL client
\set ON_ERROR_STOP true
\connect dcsa_openapi

-- Use a transaction so a bug will not leave tainted / incomplete data.
BEGIN;

\copy dcsa_ovs_v1_0.facility_type from '../referencedata.d/facilitytypes.csv' CSV HEADER
\copy dcsa_ovs_v1_0.party_function from '../referencedata.d/partyfunctioncodes.csv' CSV HEADER
\copy dcsa_ovs_v1_0.operations_event_type from '../referencedata.d/operationseventtypecodes.csv' CSV HEADER
\copy dcsa_ovs_v1_0.event_classifier from '../referencedata.d/eventclassifiercodes.csv' CSV HEADER
\copy dcsa_ovs_v1_0.port_call_service_type from '../referencedata.d/portcallservicetypecodes.csv' CSV HEADER

COMMIT;
