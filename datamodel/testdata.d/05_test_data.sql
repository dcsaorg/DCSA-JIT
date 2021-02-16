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
        'http://localhost:4567/v1/webhook/receive-schedule-1',
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
        'http://172.17.0.1:4567/v1/webhook/receive-schedule-1',
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
        'http://172.17.0.1:4567/v1/webhook/receive-schedule-2',
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
        'http://localhost:4567/v1/webhook/receive-schedule-2',
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
        'http://172.17.0.1:4567/v1/webhook/receive-transport-calls-2',
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
        'http://localhost:4567/v1/webhook/receive-transport-calls-2',
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
        'http://172.17.0.1:4567/v1/webhook/receive-transport-calls-1',
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
        'http://localhost:4567/v1/webhook/receive-transport-calls-1',
        null,
        '',
        ''
    );



    --- Insert data into v1_0 model ---


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
        'P3W',
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
        '9466966',
        'NORTHERN JASPER',
        '2007W',
        'ITGOA',
        'Genoa',
        3,
        'TERM',
        'ITGOAASEA',
        NULL
    );

    INSERT INTO schedule (id,vessel_operator_carrier_code,vessel_operator_carrier_code_list_provider,vessel_partner_carrier_code,vessel_partner_carrier_code_list_provider,start_date,date_range) VALUES
('84b10107-b116-43ea-be0d-9ff97a24a5dc','EXP','SMDG','DCSA','SMDG','2020-01-11','P3W')
;

INSERT INTO transport_call (id,schedule_id,carrier_service_code,vessel_imo_number,vessel_name,carrier_voyage_number,un_location_code,un_location_name,transport_call_number,facility_type_code,facility_code,other_facility) VALUES
('020e89cd-97ee-4b2f-a6c8-226183bd69a0','5c3ae6b7-672f-4299-adc8-6e6c35eb2b30','EXP3','5060794','Cap San Diego','EXP-12','DEHAM','Hamburg',4,'TERM','DEHAMCTT','')
,('b6eec09a-29ae-4813-897a-bbf7cb56c34a','5c3ae6b7-672f-4299-adc8-6e6c35eb2b30','EXP3','5060794','Cap San Diego','EXP-12','GBFXT','Felixtowe',5,'TERM','GBFXTDLY','')
;

INSERT INTO transport_event (event_id,event_type,event_classifier_code,event_date_time,event_type_code,transport_call_id,location_type,location_id,"comment",delay_reason_code) VALUES
('735e1b70-f394-4fe5-96ce-2dbc89e63517','transport','EST','2020-05-11 22:00:00.000','ARRI','eb0533c8-ee0b-4bb2-9c6c-af021d65f161','BERTH','Bollard 55-70',NULL,NULL)
,('d5ef5f39-bac8-4b02-9647-59b4b31743bb','transport','EST','2020-11-05 14:00:00.000','ARRI','eb0533c8-ee0b-4bb2-9c6c-af021d65f161','PBP','Bollard 55-70',NULL,NULL)
,('9242208f-75c0-4f0f-8333-06b5c52ff02f','transport','ACT','2020-11-05 15:20:00.000','ARRI','eb0533c8-ee0b-4bb2-9c6c-af021d65f161','PBP','Bollard 55-70',NULL,NULL)
,('987986a5-1cb7-4a6d-92d9-5cdff8bec6b5','transport','REQ','2020-11-05 15:30:00.000','ARRI','eb0533c8-ee0b-4bb2-9c6c-af021d65f161','PBP','Bollard 55-70','No Pilot at 14:00',NULL)
,('0727473c-9d86-47fa-9a05-8e281f6d0825','transport','PLN','2020-11-05 15:30:00.000','ARRI','eb0533c8-ee0b-4bb2-9c6c-af021d65f161','PBP','Bollard 55-70',NULL,NULL)
,('41ff70be-d7d2-48ea-be14-b42eb96c420e','transport','EST','2020-11-05 23:30:00.000','ARRI','eb0533c8-ee0b-4bb2-9c6c-af021d65f161','BERTH','Bollard 55-70',NULL,NULL)
,('987986a5-1cb7-4a6d-92d9-5cdff8bec6b5','transport','REQ','2020-11-06 01:30:00.000','ARRI','eb0533c8-ee0b-4bb2-9c6c-af021d65f161','BERTH','Bollard 55-70',NULL,NULL)
,('987986a5-1cb7-4a6d-92d9-5cdff8bec6b5','transport','REQ','2020-11-06 01:30:00.000','ARRI','eb0533c8-ee0b-4bb2-9c6c-af021d65f161','BERTH','Bollard 55-70',NULL,NULL)
,('9242208f-75c0-4f0f-8333-06b5c52ff02f','transport','ACT','2020-11-06 01:30:00.000','ARRI','eb0533c8-ee0b-4bb2-9c6c-af021d65f161','BERTH','Bollard 55-70',NULL,NULL)
,('6faa24e1-458a-4954-a6a7-6456823058af','transport','PLN','2020-11-06 01:30:00.000','ARRI','eb0533c8-ee0b-4bb2-9c6c-af021d65f161','BERTH','Bollard 55-70','RTA Confirmed; no change in PBP',NULL)
;
INSERT INTO transport_event (event_id,event_type,event_classifier_code,event_date_time,event_type_code,transport_call_id,location_type,location_id,"comment",delay_reason_code) VALUES
('35b7ed8e-00c1-4d50-8563-d08af2fa97a5','transport','PLN','2020-11-06 01:30:00.000','COPS','eb0533c8-ee0b-4bb2-9c6c-af021d65f161','CARGO_OPS','Bollard 55-70',NULL,NULL)
,('35b7ed8e-00c1-4d50-8563-d08af2fa97a5','transport','REQ','2020-11-06 01:30:00.000','COPS','eb0533c8-ee0b-4bb2-9c6c-af021d65f161','CARGO_OPS','Bollard 55-70',NULL,NULL)
,('35b7ed8e-00c1-4d50-8563-d08af2fa97a5','transport','ACT','2020-11-06 01:50:00.000','SOPS','eb0533c8-ee0b-4bb2-9c6c-af021d65f161','CARGO_OPS','Bollard 55-70',NULL,NULL)
,('35b7ed8e-00c1-4d50-8563-d08af2fa97a5','transport','EST','2020-11-06 02:00:00.000','COPS','eb0533c8-ee0b-4bb2-9c6c-af021d65f161','CARGO_OPS','Bollard 55-70',NULL,NULL)
,('35b7ed8e-00c1-4d50-8563-d08af2fa97a5','transport','ACT','2020-11-06 03:45:00.000','COPS','eb0533c8-ee0b-4bb2-9c6c-af021d65f161','CARGO_OPS','Bollard 55-70',NULL,NULL)
,('35b7ed8e-00c1-4d50-8563-d08af2fa97a5','transport','PLN','2020-11-06 03:50:00.000','COPS','eb0533c8-ee0b-4bb2-9c6c-af021d65f161','CARGO_OPS','Bollard 55-70',NULL,NULL)
,('35b7ed8e-00c1-4d50-8563-d08af2fa97a5','transport','EST','2020-11-06 03:50:00.000','COPS','eb0533c8-ee0b-4bb2-9c6c-af021d65f161','CARGO_OPS','Bollard 55-70',NULL,NULL)
,('35b7ed8e-00c1-4d50-8563-d08af2fa97a5','transport','REQ','2020-11-06 03:50:00.000','COPS','eb0533c8-ee0b-4bb2-9c6c-af021d65f161','CARGO_OPS','Bollard 55-70',NULL,NULL)
,('35b7ed8e-00c1-4d50-8563-d08af2fa97a5','transport','EST','2020-11-07 02:30:00.000','DEPT','eb0533c8-ee0b-4bb2-9c6c-af021d65f161','BERTH','Bollard 55-70','Congestion expected',NULL)
,('35b7ed8e-00c1-4d50-8563-d08af2fa97a5','transport','REQ','2020-11-07 03:30:00.000','DEPT','eb0533c8-ee0b-4bb2-9c6c-af021d65f161','BERTH','Bollard 55-70',NULL,NULL)
;
INSERT INTO transport_event (event_id,event_type,event_classifier_code,event_date_time,event_type_code,transport_call_id,location_type,location_id,"comment",delay_reason_code) VALUES
('35b7ed8e-00c1-4d50-8563-d08af2fa97a5','transport','PLN','2020-11-07 03:30:00.000','DEPT','eb0533c8-ee0b-4bb2-9c6c-af021d65f161','BERTH','Bollard 55-70','Agreed by Pilot',NULL)
,('35b7ed8e-00c1-4d50-8563-d08af2fa97a5','transport','EST','2020-11-07 04:00:00.000','DEPT','eb0533c8-ee0b-4bb2-9c6c-af021d65f161','BERTH','Bollard 55-70','New time request due to delayedcargo ops',NULL)
,('35b7ed8e-00c1-4d50-8563-d08af2fa97a5','transport','PLN','2020-11-07 04:15:00.000','DEPT','eb0533c8-ee0b-4bb2-9c6c-af021d65f161','BERTH','Bollard 55-70',NULL,NULL)
,('35b7ed8e-00c1-4d50-8563-d08af2fa97a5','transport','REQ','2020-11-07 04:15:00.000','DEPT','eb0533c8-ee0b-4bb2-9c6c-af021d65f161','BERTH','Bollard 55-70',NULL,NULL)
,('35b7ed8e-00c1-4d50-8563-d08af2fa97a5','transport','ACT','2020-11-07 04:20:00.000','DEPT','eb0533c8-ee0b-4bb2-9c6c-af021d65f161','BERTH','Bollard 55-70',NULL,NULL)
,('8afd309a-1b30-4534-b692-5b4838e01a60','transport','EST','2020-12-06 00:50:00.000','ARRI','b6eec09a-29ae-4813-897a-bbf7cb56c34a','BERTH','UNKNOWN',NULL,NULL)
;

