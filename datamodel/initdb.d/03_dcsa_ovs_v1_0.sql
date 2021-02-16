-- A script to initialize the tables relevant for the DCSA OVS v1.0

\connect dcsa_openapi



DROP TABLE IF EXISTS dcsa_ovs_v1_0.schedule_subscription CASCADE;
CREATE TABLE dcsa_ovs_v1_0.schedule_subscription (
    id uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
    callback_url text NOT NULL,
    vessel_imo_number varchar(7),
    carrier_voyage_number varchar(50),
    un_location_code varchar(5),
    date_range text,
    start_date date,
    carrier_service_code text
    );

    DROP TABLE IF EXISTS dcsa_ovs_v1_0.transport_call_subscription CASCADE;
CREATE TABLE dcsa_ovs_v1_0.transport_call_subscription (
    id uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
    callback_url text NOT NULL,
    vessel_imo_number varchar(7),
    carrier_voyage_number varchar(50),
    un_location_code varchar(5)
    );



--Helper table in order to filter Events on schedule_id
DROP TABLE IF EXISTS dcsa_ovs_v1_0.schedule CASCADE;
CREATE TABLE dcsa_ovs_v1_0.schedule (
    id uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
    vessel_operator_carrier_code varchar(10) NOT NULL,
    vessel_operator_carrier_code_list_provider text NOT NULL,
    vessel_partner_carrier_code varchar(10) NOT NULL,
    vessel_partner_carrier_code_list_provider text,
    start_date date,
    date_range text
);

DROP TABLE IF EXISTS dcsa_ovs_v1_0.transport_call CASCADE;
CREATE TABLE dcsa_ovs_v1_0.transport_call (
    id uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
    schedule_id uuid NOT NULL,
    carrier_service_code text,
    vessel_imo_number varchar(7),
    vessel_name varchar(35),
    carrier_voyage_number varchar(50) NOT NULL,
    un_location_code varchar(5) NOT NULL,
    un_location_name varchar(70),
    transport_call_number integer,
    facility_type_code varchar(4) NOT NULL,
    facility_code varchar(11) NOT NULL,
    other_facility varchar(50)
);

DROP TABLE IF EXISTS dcsa_ovs_v1_0.event CASCADE;
CREATE TABLE dcsa_ovs_v1_0.event (
    event_id uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
    event_type text NOT NULL,
    event_classifier_code varchar(3) NOT NULL,
    event_date_time timestamp with time zone NOT NULL,
    event_type_code varchar(4) NOT NULL
);

DROP TABLE IF EXISTS dcsa_ovs_v1_0.transport_event CASCADE;
CREATE TABLE dcsa_ovs_v1_0.transport_event (
    transport_call_id uuid NOT NULL,
    location_type varchar(15) NOT null,
    location_id varchar(40),
    comment varchar(40),
    delay_reason_code varchar(3)
) INHERITS (dcsa_ovs_v1_0.event);


