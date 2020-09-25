\connect dcsa_openapi



INSERT INTO dcsa_ovs_v1_0.schedule_subscription (
        callback_url,
    vessel_imo_number,
    carrier_voyage_number,
    un_location_code,
    date_range,
    start_date,
    carrier_service_code
) VALUES (
    'http://localhost:4567/webhook/receive',
    null,
    '',
    '',
    null,
    null,
    ''
);
INSERT INTO dcsa_ovs_v1_0.schedule_subscription (
        callback_url,
    vessel_imo_number,
    carrier_voyage_number,
    un_location_code,
    date_range,
    start_date,
    carrier_service_code
) VALUES (
    'http://172.17.0.1:4567/webhook/receive-schedule-2',
    null,
    '',
    'NYC',
    null,
    null,
    ''
);
INSERT INTO dcsa_ovs_v1_0.schedule_subscription (
        callback_url,
    vessel_imo_number,
    carrier_voyage_number,
    un_location_code,
    date_range,
    start_date,
    carrier_service_code
) VALUES (
    'http://localhost:4567/webhook/receive-schedule-2',
    null,
    '',
    'NYC',
    null,
    null,
    ''
);



INSERT INTO dcsa_ovs_v1_0.transport_call_subscription (
        callback_url,
    vessel_imo_number,
    carrier_voyage_number,
    un_location_code
) VALUES (
    'http://172.17.0.1:4567/webhook/receive',
    null,
    '',
    ''
);
INSERT INTO dcsa_ovs_v1_0.transport_call_subscription (
        callback_url,
    vessel_imo_number,
    carrier_voyage_number,
    un_location_code
) VALUES (
    'http://localhost:4567/webhook/receive',
    null,
    '',
    ''
);



--- Insert data into v2_0 model ---


INSERT INTO dcsa_ovs_v1_0.schedule (
    id,
    vessel_operator_carrier_code,
    vessel_partner_carrier_code,
    start_date,
    date_range,
    vessel_operator_carrier_code_list_provider,
    vessel_partner_carrier_code_list_provider
) VALUES (
    uuid('35b7b170-c751-11ea-a305-7b347bb9119f'),
    'ZIM',
    'MSK',
    DATE '2020-07-16',
    INTERVAL '3 weeks',
    'SMDG',
    'SMDG'
);

INSERT INTO dcsa_ovs_v1_0.transport_call (
    id,
    schedule_id,
    carrier_service_code,
    vessel_imo_number,
    vessel_name,
    carrier_voyage_number,
    un_location_code,
    un_location_name,
    transport_call_number,
    facility_type_code,
    facility_code,
    other_facility
) VALUES (
    uuid('8b64d20b-523b-4491-b2e5-32cfa5174eee'),
    uuid('35b7b170-c751-11ea-a305-7b347bb9119f'),
    'Y6S',
    '9466960',
    'NORTHERN JASPER',
    '2007W',
    'ITGOA',
    'Genoa',
    3,
    'TERM',
    'ITGOAASEA',
    NULL
);



