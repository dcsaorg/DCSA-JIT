insert
	into
	dcsa_ovs_v1_0.schedule_subscription ( callback_url,
	vessel_imo_number,
	carrier_voyage_number,
	un_location_code,
	date_range,
	start_date,
	carrier_service_code )
values ( 'http://localhost:4567/v1/webhook/receive-schedule-1',
null,
'',
'',
null,
null,
'' );

insert
	into
	dcsa_ovs_v1_0.schedule_subscription ( callback_url,
	vessel_imo_number,
	carrier_voyage_number,
	un_location_code,
	date_range,
	start_date,
	carrier_service_code )
values ( 'http://172.17.0.1:4567/v1/webhook/receive-schedule-1',
null,
'',
'',
null,
null,
'' );

insert
	into
	dcsa_ovs_v1_0.schedule_subscription ( callback_url,
	vessel_imo_number,
	carrier_voyage_number,
	un_location_code,
	date_range,
	start_date,
	carrier_service_code )
values ( 'http://172.17.0.1:4567/v1/webhook/receive-schedule-2',
null,
'',
'NYC',
null,
null,
'' );

insert
	into
	dcsa_ovs_v1_0.schedule_subscription ( callback_url,
	vessel_imo_number,
	carrier_voyage_number,
	un_location_code,
	date_range,
	start_date,
	carrier_service_code )
values ( 'http://localhost:4567/v1/webhook/receive-schedule-2',
null,
'',
'NYC',
null,
null,
'' );

insert
	into
	dcsa_ovs_v1_0.transport_call_subscription ( callback_url,
	vessel_imo_number,
	carrier_voyage_number,
	un_location_code )
values ( 'http://172.17.0.1:4567/v1/webhook/receive-transport-calls-2',
null,
'',
'' );

insert
	into
	dcsa_ovs_v1_0.transport_call_subscription ( callback_url,
	vessel_imo_number,
	carrier_voyage_number,
	un_location_code )
values ( 'http://localhost:4567/v1/webhook/receive-transport-calls-2',
null,
'',
'' );

insert
	into
	dcsa_ovs_v1_0.transport_call_subscription ( callback_url,
	vessel_imo_number,
	carrier_voyage_number,
	un_location_code )
values ( 'http://172.17.0.1:4567/v1/webhook/receive-transport-calls-1',
null,
'',
'' );

insert
	into
	dcsa_ovs_v1_0.transport_call_subscription ( callback_url,
	vessel_imo_number,
	carrier_voyage_number,
	un_location_code )
values ( 'http://localhost:4567/v1/webhook/receive-transport-calls-1',
null,
'',
'' );
--- Insert data into v1_0 model ---
 insert
	into
	dcsa_ovs_v1_0.schedule ( id,
	vessel_operator_carrier_code,
	vessel_partner_carrier_code,
	start_date,
	date_range,
	vessel_operator_carrier_code_list_provider,
	vessel_partner_carrier_code_list_provider )
values ( uuid('35b7b170-c751-11ea-a305-7b347bb9119f'),
'ZIM',
'MSK',
date '2020-07-16',
'P3W',
'SMDG',
'SMDG' );

insert
	into
	dcsa_ovs_v1_0.transport_call ( id,
	vessel_imo_number,
	vessel_name,
	transport_call_sequence_number,
	facility_type_code,
	facility_code,
	other_facility )
values ( uuid('8b64d20b-523b-4491-b2e5-32cfa5174eee'),
'9466960',
'NORTHERN JASPER',
3,
'POTE',
'ITGOAASEA',
null );

insert
	into
	dcsa_ovs_v1_0.schedule (id,
	vessel_operator_carrier_code,
	vessel_operator_carrier_code_list_provider,
	vessel_partner_carrier_code,
	vessel_partner_carrier_code_list_provider,
	start_date,
	date_range)
values ('84b10107-b116-43ea-be0d-9ff97a24a5dc',
'EXP',
'SMDG',
'DCSA',
'SMDG',
'2020-01-11',
'P3W') ;

insert
	into
	dcsa_ovs_v1_0.transport_call (id,
	vessel_imo_number,
	vessel_name,
	transport_call_sequence_number,
	facility_type_code,
	facility_code,
	other_facility)
values ('020e89cd-97ee-4b2f-a6c8-226183bd69a0',
'5060794',
'Cap San Diego',
4,
'POTE',
'DEHAMCTT',
'') ,
('b6eec09a-29ae-4813-897a-bbf7cb56c34a',
'5060794',
'Cap San Diego',
5,
'POTE',
'GBFXTDLY',
'') ;

-- ETA Berth
insert
	into
	dcsa_ovs_v1_0.operations_event (event_id,
	event_created_date_time,
	event_type,
	event_classifier_code,
	event_date_time,
	operations_event_type_code,
	publisher,
	publisher_role,
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
'DEHAMCTA',
'CA',
'eb0533c8-ee0b-4bb2-9c6c-af021d65f161',
'Bollard 55-70',
null,
'BRTH',
null);

-- ETA PBP

insert
	into
	dcsa_ovs_v1_0.operations_event (event_id,
	event_created_date_time,
	event_type,
	event_classifier_code,
	event_date_time,
	operations_event_type_code,
	publisher,
	publisher_role,
	transport_call_id,
	event_location,
	port_call_service_type_code,
	facility_type_code,
	delay_reason_code)
values
('d5ef5f39-bac8-4b02-9647-59b4b31743bb',
'2020-05-10 18:00:00.000',
'OPERATIONS',
'EST',
'2020-11-05 14:00:00.000',
'ARRI',
'DEHAMCTA',
'TR',
'eb0533c8-ee0b-4bb2-9c6c-af021d65f161',
'Bollard 55-70',
null,
'PBPL',
null);


-- ATA PBP
insert
	into
	dcsa_ovs_v1_0.operations_event (event_id,
	event_created_date_time,
	event_type,
	event_classifier_code,
	event_date_time,
	operations_event_type_code,
	publisher,
	publisher_role,
	transport_call_id,
	event_location,
	port_call_service_type_code,
	facility_type_code,
	delay_reason_code)
values
('9242208f-75c0-4f0f-8333-06b5c52ff02f',
'2020-05-10 18:00:00.000',
'OPERATIONS',
'ACT',
'2020-11-05 15:20:00.000',
'ARRI',
'DEHAM',
'POR',
'eb0533c8-ee0b-4bb2-9c6c-af021d65f161',
'Bollard 55-70',
null,
'PBPL',
null);

-- RTA PBL
insert
	into
	dcsa_ovs_v1_0.operations_event (event_id,
	event_created_date_time,
	event_type,
	event_classifier_code,
	event_date_time,
	operations_event_type_code,
	publisher,
	publisher_role,
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
'DEHAM',
'CA',
'eb0533c8-ee0b-4bb2-9c6c-af021d65f161',
'Bollard 55-70',
null,
'PBPL',
'WEA');



-- PTA PBL
insert
	into
	dcsa_ovs_v1_0.operations_event (event_id,
	event_created_date_time,
	event_type,
	event_classifier_code,
	event_date_time,
	operations_event_type_code,
	publisher,
	publisher_role,
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
'DEHAM',
'POR',
'eb0533c8-ee0b-4bb2-9c6c-af021d65f161',
'Bollard 55-70',
null,
'PBPL',
null);



-- ETC CARGO OPS
insert
	into
	dcsa_ovs_v1_0.operations_event (event_id,
	event_created_date_time,
	event_type,
	event_classifier_code,
	event_date_time,
	operations_event_type_code,
	publisher,
	publisher_role,
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
'DEHAM',
'POR',
'eb0533c8-ee0b-4bb2-9c6c-af021d65f161',
'Bollard 55-70',
'CRGO',
null,
null);


-- ATS CARGO OPS
insert
	into
	dcsa_ovs_v1_0.operations_event (event_id,
	event_created_date_time,
	event_type,
	event_classifier_code,
	event_date_time,
	operations_event_type_code,
	publisher,
	publisher_role,
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
'DEHAM',
'POR',
'eb0533c8-ee0b-4bb2-9c6c-af021d65f161',
'Bollard 55-70',
'CRGO',
null,
null);



-- ETD Berth
insert
	into
	dcsa_ovs_v1_0.operations_event (event_id,
	event_created_date_time,
	event_type,
	event_classifier_code,
	event_date_time,
	operations_event_type_code,
	publisher,
	publisher_role,
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
'DEHAMCTA',
'CA',
'eb0533c8-ee0b-4bb2-9c6c-af021d65f161',
'Bollard 55-70',
null,
'BRTH',
null);


