-- A script to initialize the tables relevant for the DCSA OVS v1.0

\connect dcsa_openapi



DROP TABLE IF EXISTS dcsa_ovs_v1_0.schedule_subscription CASCADE;
CREATE TABLE dcsa_ovs_v1_0.schedule_subscription (
    id uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
    callback_url text NOT NULL,
    vessel_imo_number numeric,
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
    vessel_imo_number numeric,
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
    date_range interval
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

-- Helper table in order to filter Events on equipment-reference
DROP TABLE IF EXISTS dcsa_ovs_v1_0.shipment CASCADE;
CREATE TABLE dcsa_ovs_v1_0.shipment (
    id uuid NOT NULL,
    booking_reference text, -- The identifier for a shipment, which is issued by and unique within each of the carriers.
    booking_datetime timestamp, -- The date and time of the booking request.
    transport_document_id UUID, -- Transport Document ID is an identifier that links to a shipment. Bill of lading is the legal document issued to the customer which confirms the carrier's receipt of the cargo from the customer acknowledging goods being shipped and specifying the terms of delivery.
    transport_document_type_code text,
    shipper_name varchar(50), -- The name of the shipper, who requested the booking
    consignee_name varchar(50), -- The name of the consignee
    collection_origin varchar(250), -- The location through which the shipment originates. It can be defined as a UN Location Code value or an address. The customer (shipper) needs to place a booking in order to ship the cargo (commodity) from an origin to destination. This attribute specifies the location of the origin.
    collection_dateTime timestamp, -- The date and the time that the shipment items need to be collected from the origin.
    delivery_destination varchar(250), -- The location to which the shipment is destined. It can be defined as a UN Location Code value or an address. The customer (shipper) needs to place a booking in order to ship the cargo (commodity) from an origin to destination. This attribute specifies the location of the destination. Also known as 'place of carrier delivery'.
    delivery_datetime timestamp , -- The date (and when possible time) that the shipment items need to be delivered to the destination.
    carrier_code varchar(10) -- The Carrier Code represents a concatenation of the Code List Provider Code and the Code List Provider. A hyphen is used between the two codes. The unique carrier identifier is sourced from either the NMFTA SCAC codes list or the SMDG Master Liner codes list.
    );
