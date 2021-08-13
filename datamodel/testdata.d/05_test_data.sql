\connect dcsa_openapi

-- Test data solely for OVS, currently without automatic deployment!

-- Insert a dummy Location Code
INSERT INTO "location" (id,location_name,address_id,latitude,longitude,un_location_code) VALUES
('8d44c5bf-16b8-49d6-9043-356983f99b5b','Meter 100 - 200',NULL,NULL,NULL,NULL)
;

insert
	into
	dcsa_im_v3_0.vessel (vessel_imo_number,
	vessel_name,
	vessel_flag,
	vessel_call_sign_number,
	vessel_operator_carrier_id)
values ('9074729',
'Example Vessel',
'NL',
'XYZ',
null) ,
('5060794',
'Cap San Diego',
'DE',
'DNAI',
null) ;

insert
	into
	dcsa_im_v3_0.transport_call ( id,
	vessel,
	transport_call_sequence_number,
	facility_type_code,
	facility_code,
	other_facility )
values ( uuid('8b64d20b-523b-4491-b2e5-32cfa5174eee'),
'5060794',
3,
'POTE',
'DEHAMCTA',
null );

insert
	into
	dcsa_im_v3_0.transport_call (id,
	vessel,
	transport_call_sequence_number,
	facility_type_code,
	facility_code,
	other_facility)
values ('020e89cd-97ee-4b2f-a6c8-226183bd69a0',
'5060794',
4,
'POTE',
'DEHAMCTT',
'') ,
('b6eec09a-29ae-4813-897a-bbf7cb56c34a',
'5060794',
5,
'POTE',
'GBFXTDLY',
'') ;

-- ETA Berth
 insert
	into
	dcsa_im_v3_0.operations_event (event_id,
	event_created_date_time,
	event_type,
	event_classifier_code,
	event_date_time,
	operations_event_type_code,
	publisher,
	publisher_role,
	publisher_code_list_provider,
	transport_call_id,
	event_location,
	port_call_service_type_code,
	facility_type_code,
	delay_reason_code)
values ('735e1b70-f394-4fe5-96ce-2dbc89e63517',
'2020-05-11 22:00:00.000',
'OPERATIONS',
'ACT',
'2020-11-05 14:00:00.000',
'ARRI',
'CTA',
'CA',
'',
'020e89cd-97ee-4b2f-a6c8-226183bd69a0',
'8d44c5bf-16b8-49d6-9043-356983f99b5b',
null,
'BRTH',
null);
-- ETA PBP
 insert
	into
	dcsa_im_v3_0.operations_event (event_id,
	event_created_date_time,
	event_type,
	event_classifier_code,
	event_date_time,
	operations_event_type_code,
	publisher,
	publisher_role,
	publisher_code_list_provider,
	transport_call_id,
	event_location,
	port_call_service_type_code,
	facility_type_code,
	delay_reason_code)
values ('d5ef5f39-bac8-4b02-9647-59b4b31743bb',
'2020-05-10 18:00:00.000',
'OPERATIONS',
'EST',
'2020-11-05 14:00:00.000',
'ARRI',
'CTA',
'TR',
'',
'020e89cd-97ee-4b2f-a6c8-226183bd69a0',
'8d44c5bf-16b8-49d6-9043-356983f99b5b',
null,
'PBPL',
null);
-- ATA PBP
 insert
	into
	dcsa_im_v3_0.operations_event (event_id,
	event_created_date_time,
	event_type,
	event_classifier_code,
	event_date_time,
	operations_event_type_code,
	publisher,
	publisher_role,
	publisher_code_list_provider,
	transport_call_id,
	event_location,
	port_call_service_type_code,
	facility_type_code,
	delay_reason_code)
values ('9242208f-75c0-4f0f-8333-06b5c52ff02f',
'2020-05-10 18:00:00.000',
'OPERATIONS',
'ACT',
'2020-11-05 15:20:00.000',
'ARRI',
'HAM',
'POR',
'',
'020e89cd-97ee-4b2f-a6c8-226183bd69a0',
'8d44c5bf-16b8-49d6-9043-356983f99b5b',
null,
'PBPL',
null);
-- RTA PBL
 insert
	into
	dcsa_im_v3_0.operations_event (event_id,
	event_created_date_time,
	event_type,
	event_classifier_code,
	event_date_time,
	operations_event_type_code,
	publisher,
	publisher_role,
	publisher_code_list_provider,
	transport_call_id,
	event_location,
	port_call_service_type_code,
	facility_type_code,
	delay_reason_code)
values ('987986a5-1cb7-4a6d-92d9-5cdff8bec6b5',
'2020-05-10 18:00:00.000',
'OPERATIONS',
'REQ',
'2020-11-05 15:30:00.000',
'ARRI',
'HAM',
'CA',
'',
'020e89cd-97ee-4b2f-a6c8-226183bd69a0',
'8d44c5bf-16b8-49d6-9043-356983f99b5b',
null,
'PBPL',
'WEA');
-- PTA PBL
 insert
	into
	dcsa_im_v3_0.operations_event (event_id,
	event_created_date_time,
	event_type,
	event_classifier_code,
	event_date_time,
	operations_event_type_code,
	publisher,
	publisher_role,
	publisher_code_list_provider,
	transport_call_id,
	event_location,
	port_call_service_type_code,
	facility_type_code,
	delay_reason_code)
values ('987986a5-1cb7-4a6d-92d9-5cdff8bec6b5',
'2020-05-10 18:00:00.000',
'OPERATIONS',
'PLN',
'2020-11-05 15:30:00.000',
'ARRI',
'HAM',
'POR',
'',
'020e89cd-97ee-4b2f-a6c8-226183bd69a0',
'8d44c5bf-16b8-49d6-9043-356983f99b5b',
null,
'PBPL',
null);
-- ETC CARGO OPS
 insert
	into
	dcsa_im_v3_0.operations_event (event_id,
	event_created_date_time,
	event_type,
	event_classifier_code,
	event_date_time,
	operations_event_type_code,
	publisher,
	publisher_role,
	publisher_code_list_provider,
	transport_call_id,
	event_location,
	port_call_service_type_code,
	facility_type_code,
	delay_reason_code)
values ('987986a5-1cb7-4a6d-92d9-5cdff8bec6b5',
'2020-05-10 18:00:00.000',
'OPERATIONS',
'EST',
'2020-11-05 15:30:00.000',
'CMPL',
'HAM',
'POR',
'',
'020e89cd-97ee-4b2f-a6c8-226183bd69a0',
'8d44c5bf-16b8-49d6-9043-356983f99b5b',
'CRGO',
null,
null);
-- ATS CARGO OPS
 insert
	into
	dcsa_im_v3_0.operations_event (event_id,
	event_created_date_time,
	event_type,
	event_classifier_code,
	event_date_time,
	operations_event_type_code,
	publisher,
	publisher_role,
	publisher_code_list_provider,
	transport_call_id,
	event_location,
	port_call_service_type_code,
	facility_type_code,
	delay_reason_code)
values ('987986a5-1cb7-4a6d-92d9-5cdff8bec6b5',
'2020-05-10 18:00:00.000',
'OPERATIONS',
'EST',
'2020-11-05 15:30:00.000',
'STRT',
'HAM',
'POR',
'',
'020e89cd-97ee-4b2f-a6c8-226183bd69a0',
'8d44c5bf-16b8-49d6-9043-356983f99b5b',
'CRGO',
null,
null);
-- ETD Berth
 insert
	into
	dcsa_im_v3_0.operations_event (event_id,
	event_created_date_time,
	event_type,
	event_classifier_code,
	event_date_time,
	operations_event_type_code,
	publisher,
	publisher_role,
	publisher_code_list_provider,
	transport_call_id,
	event_location,
	port_call_service_type_code,
	facility_type_code,
	delay_reason_code)
values ('735e1b70-f394-4fe5-96ce-2dbc89e63517',
'2020-05-11 22:00:00.000',
'OPERATIONS',
'ACT',
'2020-11-05 14:00:00.000',
'DEPA',
'CTA',
'CA',
'',
'020e89cd-97ee-4b2f-a6c8-226183bd69a0',
'8d44c5bf-16b8-49d6-9043-356983f99b5b',
null,
'BRTH',
null);
-- ATS PILOTING
 insert
	into
	dcsa_im_v3_0.operations_event (event_id,
	event_created_date_time,
	event_type,
	event_classifier_code,
	event_date_time,
	operations_event_type_code,
	publisher,
	publisher_role,
	publisher_code_list_provider,
	transport_call_id,
	event_location,
	port_call_service_type_code,
	facility_type_code,
	delay_reason_code,
	change_remark)
values ('d6ecbde8-6c9b-4cd2-a637-ebc412b29620',
'2021-04-07 17:54:00.000',
'OPERATIONS',
'ACT',
'2021-04-07 16:00:00.000',
'STRT',
'HAM',
'POR',
'',
'020e89cd-97ee-4b2f-a6c8-226183bd69a0',
'8d44c5bf-16b8-49d6-9043-356983f99b5b',
'PILO',
null,
null,
null)