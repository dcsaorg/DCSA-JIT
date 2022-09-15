# Module 9 part 1 - preparing an ETA-Berth

This is part 1 of Module 9 of the DCSA JIT Adoption pack.
The document is written for `JIT 1.2.0-beta1`.

This document will cover how you send a DCSA JIT compliant
estimated time of arrival at berth (`ETA-Berth`, sometimes also
called an `ETB` in daily operations) at the technical level.

To keep things simple, this module is written from a carrier's
point of view using the first two timestamp of the process.
Despite the point of view, the logic used in this module applies
to all actors (but for a different set of timestamps).

The reader is assumed to:

 * Be familiar with JSON
 * Understand the "Estimated -> Requested -> Planned" (`E -> R -> P`)
   negotiation cycle covered in Module 4.

It is a bonus if you are also familiar with the Swagger spec of JIT
as it has more contextual information about all the fields.

## Context of this document

In this document, we will go over a small part of the DCSA JIT
process by showing how to send a timestamp, which is a mandatory
part of all actors in the DCSA JIT process.  To keep things simple
and concrete, this document will cover a carrier sending out an
`ETA-Berth`, which is the very first part of the DCSA JIT process.
However, the concepts used in this document holds for all timestamps.

The examples in this howto will use Evergreen Marine (`EMC`) as
the carrier and the Eurogate (`EGH`) in Hamburg (`DEHAM`)
as the terminal to keep things consistent throughout this
document.

In this setup, we assume both the carrier and the terminal host
their own instance of the JIT server to keep this document simple.
More details on integration methods are covered in Module 10.

## Duplicated information in the data

Throughout the examples, you may note that some data appears twice
in the JSON examples.  This is generally due to backwards compatibility
requirements in the current API.  Please review the schema of the swagger
specs to see which of the fields are the deprecated names and which are
the new names.

Optional deprecated fields will generally be omitted from the examples.

# Starting with the ETA-Berth

To initiate the JIT port call process, the carrier will send an ETA-Berth
to the terminal. To do that, the carrier must construct a valid JSON payload
that represent the timestamp information they want to send. To construct
the JSON message, we have to collect information of the following
categories:

 * Publisher & role - Who sent this timestamp?
 * Event Info - The basic part of the event (when/what).
   * This includes the 5 fields required for determining what kind of timestamp
     this is (ETA-Berth vs. RTA-Berth, etc,).  These 5 fields are part of the
     timestamp classification.
 * Event location description - Where will this take place.
 * Vessel information - What vessel is this timestamp about.
 * Port Call information - Information about the port call (such as carrier voyage number)

These categories are unofficial grouping to break down the timestamp into
smaller parts.

In the following subsections, we will go over each category and describe
how to find this information.

## Publisher & role

The publisher and role category is (mostly) static information about the
organization sending it.  For most actors, they can set this up once and
just reuse that in every timestamp.

To construct the publisher information, you will need to know:

 * How do we identify this organization?
   - Where possibly, DCSA recommends using identifying party codes
     (notably, UN/ECE Location Codes and SMDG liner/terminal codes).
 * Which role are we acting on behalf of?  Note: Carriers have multiple
   options.  The actual roles are defined in the IFS or on Swagger.

The relevant identifying party codes are provided by SMDG in their
SMDG Liner Code List (LCL) and SMDG Terminal Code List (TCL) at
https://smdg.org/documents/smdg-code-lists/.

In this howto, we will be showing the following examples:

 * Evergreen Marine (`EMC`). For this we will use the "Carrier" role (`CA`),
    - In the real world it could also have been the "Local Agent" (`AG`)
      or the "Vessel" (`VSL`) that sent this information. That depends
      on how the carrier is organized.
 * Eurogate terminal (`EGH`/`DEHAM`). Terminals always use `TR` as role.

Note all the examples here are just _suggestions_ for how these actors _could_
present themselves. The examples also deliberately prefer simplicity and brevity
where possible.

### Carrier Example
```json
{
  "publisher": {
    "partyName": "Evergreen Vessel Operations",
    "identifyingCodes": [
      {
        "DCSAResponsibleAgencyCode": "SMDG",
        "partyCode": "EMC",
        "codeListName": "LCL"
      }
    ]
  },
  "publisherRole": "CA"
}
```
Note the `codeListName` which is here set to `LCL` to clarify that `EMC` is from the
SMDG Liner Code List (LCL).

### Terminal Example
```json
{
  "publisher": {
    "partyName": "Hamburg Eurogate terminal operations",
    "identifyingCodes": [
      {
        "DCSAResponsibleAgencyCode": "UNECE",
        "partyCode": "DEHAM"
      },
      {
        "DCSAResponsibleAgencyCode": "SMDG",
        "partyCode": "EGH",
        "codeListName": "TCL"
      }
    ]
  },
  "publisherRole": "TR"
}
```
Here we have two codes because the SMDG terminal codes are only defined with a UN Location Code
(the `UNECE` code).  Again, we set the `codeListName` for the SMDG code. This time to `TCL` to
clarify that `EGH` is from the SMDG Terminal Code List (TCL).

This concrete example will first be used in the next part of this module.


With these two examples, we are ready to move on to the next category.


## Event Info

The event info category covers what kind of timestamp it is, when it occurred
or is expected to occur and a bit of context.

For this category, we will need to know:

 * What kind of timestamp are we sending?
   - In this example, we are sending an ETA-Berth.
 * When will it (or do we expect it) to happen?
   - For this example, let us assume we expect the vessel to arrive on the 15th of September at 08:10 (07:10 UTC)
 * What kind of context do we give to the receiver?
   - In this example, let us assume we were sending this estimate because we expect to be delayed by 8 hours due to wind conditions.

With these, we might end up with something like this:

```json
{
  "eventClassifierCode": "EST",
  "operationsEventTypeCode": "ARRI",
  "facilityTypeCode": "BRTH",
  "portCallPhaseTypeCode": null,
  "portCallServiceTypeCode": null,
  "eventDateTime": "2022-09-15T08:10:00.000+01:00",
  "delayReasonCode": "WEA",
  "remark": "8 hour delay from the original schedule due to weather. ETA based on maintaining 20 knots from here on."
}
```

_Note that in this example, we render the time in the port's timezone to make it easier to align the text with the JSON. Using UTC would have been equally valid._

To understand how we got from `ETA-Berth`to the above snippet, we have to understand how timestamps
are classified in JIT.  We will cover in a later section of the module.  For now, we are moving
on to the next category.

## Event location description

The `ETA-berth` is going to happen at the Eurogate terminal in Hamburg.  This could
be represented via the following JSON.

```json
{
  "eventLocation": {
    "UNLocationCode": "DEHAM",
    "facilityCode": "EGH",
    "facilityCodeListProvider": "SMDG"
  },
  "UNLocationCode": "DEHAM",
  "facilitySMDGCode": "EGH"
}
```

Leveraging the UN Location Code and the SMDG Terminal Code List codes, we can
identify _where_ this will happen.  Note that we are not specifying which
berth will be used.  The terminal will generally decide that so that will be in
their `RTA-Berth` response.


Locations vary a lot between different types of timestamps.  As an example,
that the PBP timestamps (e.g., `ETA-PBP`) do not specify a terminal (facility).
This means that `facilityCode`, `facilityCodeListProvider` and `facilitySMDGCode`
would be `null` (or omitted) in that case.

The rule of thumb is that when `facilityTypeCode` field (from the `Event Info`
category) is Berth (`BRTH`) you need facility. Otherwise, the facility is omitted from
the location.

With this, we have covered what is needed for the location in an `ETA-Berth`.

## Vessel information

The timestamp should also cover some information about the vessel. The
bare minimum is the vessel IMO number (to identify the vessel).

For the example, lets assume the vessel is `Ever Given` (`IMO 9811000`).
To give some context to the terminal, we are going to include static
information (name, basic dimensions of the vessel and the fact that it is
a container ship).

We are also going to provide information about the vessel draft (which
depends on how loaded the vessel is) and how far we are from the
port at the time of sending the message.  For the sake of the example,
we assume the vessel is fully loaded (draft of 16 meters) and it is 20
nautical miles from the port, then the message could look something
like:

```json
{
  "vessel": {
    "vesselIMONumber": "9811000",
    "name": "Ever Given",
    "lengthOverall": "399.94",
    "width": "58.8",
    "draft": "16.0",
    "dimensionUnit": "MTR",
    "type": "CONT"
  },
  "vesselIMONumber": "9811000",
  "milesToDestinationPort": "20"
}
```

We could also have used a vessel position (rather than `milesToDestinationPort`).
However, positions are harder to use for the terminal when evaluating the `ETA-Berth`
and for how reasonable their counter proposed `RTA-Berth` would be.  So where possible,
you are recommended to use `milesToDestinationPort` rather than `vesselPosition` when
negotiation arrival at berth and arrival at pilot boarding place (e.g., `ETA-PBP`).

Generally, you should only provide the information that makes sense for the timestamp
in question. As an example, we include the vessel draft information in this example
because draft plays an important role when planning arrival and departure in Hamburg.


## Port Call information

Actors can also provide some port call information.  This includes things like the
carrier service code and import and export voyage number.  A simple example could
look something like this (assuming `CSC1` is the service code and the vessel is changing
voyage numbers in Hamburg):

```json
{
  "carrierExportVoyageNumber": "2202E",
  "carrierImportVoyageNumber": "2202W",
  "carrierVoyageNumber": "2202E",
  "carrierServiceCode": "CSC1",
  "transportCallSequenceNumber": 1
}
```

Generally, this information is always provided by the carrier and other parties will echo it
to ensure everyone is talking about the same port call.

The `transportCallSequenceNumber` is only used in case of a double call (where the vessel is visiting
the same port or/and terminal twice in the same voyage).  Parties are recommended to default this value
to `1` (as shown in the example) for the first call and then use `2` for the second call (etc.).

This category also includes a `portVisitReference` (not shown) which can be used to communicate the
reference number assigned by a port authority to the stay of a vessel in the port (`IMO0153`).


## Combining all of it

Once we combine all of this, the `ETA-Berth` will look like this:

```json
{
  "publisher": {
    "partyName": "Evergreen Vessel Operations",
    "identifyingCodes": [
      {
        "DCSAResponsibleAgencyCode": "SMDG",
        "partyCode": "EMC",
        "codeListName": "LCL"
      }
    ]
  },
  "publisherRole": "CA",


  "eventClassifierCode": "EST",
  "operationsEventTypeCode": "ARRI",
  "facilityTypeCode": "BRTH",
  "portCallPhaseTypeCode": null,
  "portCallServiceTypeCode": null,
  "eventDateTime": "2022-09-15T08:10:00.000+01:00",
  "delayReasonCode": "WEA",
  "remark": "8 hour delay from the original schedule due to weather. ETA based on maintaining 20 knots from here on.",


  "eventLocation": {
    "UNLocationCode": "DEHAM",
    "facilityCode": "EGH",
    "facilityCodeListProvider": "SMDG"
  },
  "UNLocationCode": "DEHAM",
  "facilitySMDGCode": "EGH",


  "vessel": {
    "vesselIMONumber": "9811000",
    "name": "Ever Given",
    "lengthOverall": "399.94",
    "width": "58.8",
    "draft": "16.0",
    "dimensionUnit": "MTR",
    "type": "CONT"
  },
  "vesselIMONumber": "9811000",
  "milesToDestinationPort": "20",


  "carrierExportVoyageNumber": "2202E",
  "carrierImportVoyageNumber": "2202W",
  "carrierVoyageNumber": "2202E",
  "carrierServiceCode": "CSC1",
  "transportCallSequenceNumber": 1
}
```

_The spacing is purely for showing each category._

Now that we have this payload, we can submit this to the terminal by `POST`'ing it to their JIT server
on the `/v1/timestamps` endpoint.  The terminal will confirm having received the timestamp with an HTTP
`204`. The actual reply will come asynchronously.

This concludes part of 1 of Module 9 and covered how to create an `ETA-Berth`.

You can find the [next part of this module here].

[next part of this module here]: Module%209%20part%202%20-%20receiving%20an%20RTA-Berth.md
