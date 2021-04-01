-- A script to initialize the tables relevant for the DCSA OVS v1.0

\connect dcsa_openapi



-- OVS Required Reference Data
DROP TABLE IF EXISTS dcsa_ovs_v1_0.operations_event_type CASCADE;
CREATE TABLE dcsa_ovs_v1_0.operations_event_type (
    operations_event_type_code varchar(4) PRIMARY KEY,
    operations_event_type_name varchar(30) NOT NULL
);

DROP TABLE IF EXISTS dcsa_ovs_v1_0.facility_type CASCADE;
CREATE TABLE dcsa_ovs_v1_0.facility_type (
    facility_type_code varchar(4) PRIMARY KEY,
    facility_type_name varchar(100) NULL,
    facility_type_description varchar(250) NULL
);

DROP TABLE IF EXISTS dcsa_ovs_v1_0.party_function CASCADE;
CREATE TABLE dcsa_ovs_v1_0.party_function (
    party_function_code varchar(3) PRIMARY KEY,
    party_function_name varchar(100) NOT NULL,
    party_function_description varchar(250) NOT NULL
);

DROP TABLE IF EXISTS dcsa_ovs_v1_0.port_call_service_type CASCADE;
CREATE TABLE dcsa_ovs_v1_0.port_call_service_type (
    port_call_service_type_code varchar(4) PRIMARY KEY, -- The 4-letter code indicating the type of the port call service.
    port_call_service_type_name varchar(50) NOT NULL, -- The name of the of the port call service type.
    port_call_service_type_description varchar(300) NOT NULL -- The description of the of the port call service type.
);


DROP TABLE IF EXISTS dcsa_ovs_v1_0.event_classifier CASCADE;
CREATE TABLE dcsa_ovs_v1_0.event_classifier (
    event_classifier_code char(3) PRIMARY KEY, -- Code for the event classifier, either PLN, ACT or EST.
    event_classifier_name varchar(30) NULL, -- Name of the classifier.
    event_classifier_description varchar(250) NULL -- The description of the event classifier.
);


--  Schedule related entities
-- @ToDo Not Updated from IM yet, might change

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

-- Transport Call Related Entities

-- @ToDo check as transportCall subscription might be obsolete
    DROP TABLE IF EXISTS dcsa_ovs_v1_0.transport_call_subscription CASCADE;
CREATE TABLE dcsa_ovs_v1_0.transport_call_subscription (
    id uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
    callback_url text NOT NULL,
    vessel_imo_number varchar(7),
    carrier_voyage_number varchar(50),
    un_location_code varchar(5)
    );

DROP TABLE IF EXISTS dcsa_ovs_v1_0.transport_call CASCADE;
CREATE TABLE dcsa_ovs_v1_0.transport_call (
    id uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
	transport_call_sequence_number integer,
	vessel_imo_number varchar(7),
    vessel_name varchar(35),
    -- For Test reasons the facility code is not referencing the facility entity
	facility_code varchar(11) NULL,
	facility_type_code char(4) NULL REFERENCES dcsa_ovs_v1_0.facility_type (facility_type_code),
	other_facility varchar(50) NULL,
	location_id uuid NULL
);

-- Events related Entities
DROP TABLE IF EXISTS dcsa_ovs_v1_0.event CASCADE;
CREATE TABLE dcsa_ovs_v1_0.event (
    event_id uuid DEFAULT uuid_generate_v4() PRIMARY KEY, -- Unique identifier for the event captured.
    event_created_date_time timestamp with time zone NOT NULL DEFAULT now(), -- The date and time when the event record was created and stored.
    event_type text NOT NULL,
    event_classifier_code varchar(3) NOT NULL REFERENCES dcsa_ovs_v1_0.event_classifier, -- Code for the event classifier telling whether the information relates to an actual or future event.
    event_date_time timestamp with time zone NOT NULL -- Indicating the date and time of when the event occurred or will occur.
);

DROP TABLE IF EXISTS dcsa_ovs_v1_0.operations_event CASCADE;
CREATE TABLE dcsa_ovs_v1_0.operations_event (
    operations_event_type_code varchar(4) NOT NULL REFERENCES dcsa_ovs_v1_0.operations_event_type(operations_event_type_code), -- The code to identify the type of event that is related to the operation.
    publisher varchar(50) NOT NULL, -- The publisher (source) of the event
    publisher_role varchar(3) NOT NULL REFERENCES dcsa_ovs_v1_0.party_function, -- The party function code of the publisher
    transport_call_id uuid NOT NULL,
    event_location varchar(50) NOT NULL, -- The location where the event takes place.
    port_call_service_type_code varchar(4) REFERENCES dcsa_ovs_v1_0.port_call_service_type(port_call_service_type_code), -- The type of the service provided in the port call.
    facility_type_code varchar(4) NULL, -- Four character code to identify the specific type of facility.
    delay_reason_code varchar(3) -- SMDG code indicating the reason for a delay
) INHERITS (dcsa_ovs_v1_0.event);

ALTER TABLE dcsa_ovs_v1_0.operations_event
ADD FOREIGN KEY (event_classifier_code)
REFERENCES dcsa_ovs_v1_0.event_classifier (event_classifier_code);


