# Module 9 part 2 - receiving an RTA-Berth

This is part 2 of Module 9 of the DCSA JIT Adoption pack.
The document is written for `JIT 1.2.0-beta1`.

This document will cover how you interpret a DCSA JIT compliant
requested time of arrival at berth (`RTA-Berth`) at the technical
level that you received from another party.

To keep things simple, this module is written from a carrier's
point of view using the first two timestamp of the process.
Despite the point of view, the logic used in this module applies
to all actors (but for a different set of timestamps).

The reader is assumed to:

 * Have read [the previous part of this module].
 * Be familiar with JSON
 * Understand the "Estimated -> Requested -> Planned" (`E -> R -> P`)
   negotiation cycle covered in [Module 4].

It is a bonus if you are also familiar with the [Swagger spec of JIT]
as it has more contextual information about all the fields.

## Context of this document

In this document, we will go over a small part of the DCSA JIT
process.  The document will cover a carrier receiving an `RTA-Berth`
from the terminal.

The examples in this howto will use Evergreen Marine (`EMC`) as
the carrier and the Eurogate (`EGH`) in Hamburg (`DEHAM`)
as the terminal to keep things consistent throughout this
document.

In this setup, we assume both the carrier and the terminal host
their own instance of the JIT server to keep this document simple.
More details on integration methods are covered in Module 10.

Please check [the previous part of this module] if you need a
refresh on what was sent out as the examples in this part will
continue with that thread.

## Duplicated information in the data

Throughout the examples, you may note that some data appears twice
in the JSON examples.  This is generally due to backwards compatibility
requirements in the current API.  Please review the schema of the swagger
specs to see which of the fields are the deprecated names and which are
the new names.

Optional deprecated fields will generally be omitted from the examples.

# Receiving an RTA-Berth

After the carrier has sent its `ETA-Berth`, the terminal will evaluate it
and eventually reply with an `RTA-Berth`.  In this document, we will assume
that the terminal will `POST` a timestamp to a carrier hosted JIT server
using the `/v1/timestamps` endpoint.

Let us assume the terminal provides the following payload:


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
  "publisherRole": "TR",


  "eventClassifierCode": "REQ",
  "operationsEventTypeCode": "ARRI",
  "facilityTypeCode": "BRTH",
  "portCallPhaseTypeCode": null,
  "portCallServiceTypeCode": null,
  "eventDateTime": "2022-09-15T12:10:00.000+01:00",
  "delayReasonCode": "CRN",
  "remark": "We do not have enough cranes available for you before 12:00.",


  "eventLocation": {
    "locationName": "Waltershofer Hafen/S",
    "UNLocationCode": "DEHAM",
    "facilityCode": "EGH",
    "facilityCodeListProvider": "SMDG"
  },
  "UNLocationCode": "DEHAM",
  "facilitySMDGCode": "EGH",


  "vessel": {
    "vesselIMONumber": "9811000"
  },
  "vesselIMONumber": "9811000",


  "carrierExportVoyageNumber": "2202E",
  "carrierImportVoyageNumber": "2202W",
  "carrierVoyageNumber": "2202E",
  "carrierServiceCode": "CSC1",
  "transportCallSequenceNumber": 1
}
```

_The spacing and order of the fields is purely for grouping fields into the same category as used in the previous part of this module.  JSON does not mandate a particular field order nor any of the whitespace._

Here is a quick overview at what happened per category:

* Publisher & role: This now uses the terminal as publisher rather than the carrier.
  - The actual publisher details used for the terminal was shown in the previous part and there should be no surprises
    here that the publisher changed when the terminal sent out the message.
* Event Info: This category will be covered in the section below.
* Event location description: Mostly the same but we now have a `locationName` as well. This will be covered below.
* Vessel information: Most of the information has been trimmed as they are not relevant in the reply leaving just
  the vessel IMO number (unchanged).
* Port Call information: This category is completely unchanged.


## Event Info in details

Having a closer look at the event information - just highlighting it here to ease reviewing:

```json
{
  "eventClassifierCode": "REQ",
  "operationsEventTypeCode": "ARRI",
  "facilityTypeCode": "BRTH",
  "portCallPhaseTypeCode": null,
  "portCallServiceTypeCode": null,
  "eventDateTime": "2022-09-15T12:10:00.000+01:00",
  "delayReasonCode": "CRN",
  "remark": "We do not have enough cranes available for you before 12:00."
}
```

We see here that:

 1. This is an `RTA-Berth`.  The quick way of knowing this is given we know we sent an `ETA-Berth` and `eventClassifierCode`
    changed from `EST` to `REQ` tells us we now received an `R<something>` timestamp.  With `operationsEventTypeCode`,
    `facilityTypeCode`, `portCalPhaseTypeCode` and `portCallServiceTypeCode` being the same, we know we went from an `ETA-Berth`
    to an `RTA-Berth`.
    - Note we will cover a more detailed break down of these fields in the next part.
 2. The terminal requested an arrival time of `12:10` rather than the `08:10` (which we sent in the previous part) meaning they want us to arrive a few hours later.
 3. Their reason being they do not have enough cranes for processing our vessel before `12:00`.


## Event location description

Zooming in on the event location:

```json
{
  "eventLocation": {
    "locationName": "Waltershofer Hafen/S",
    "UNLocationCode": "DEHAM",
    "facilityCode": "EGH",
    "facilityCodeListProvider": "SMDG"
  },
  "UNLocationCode": "DEHAM",
  "facilitySMDGCode": "EGH"
}
```

As we can see, the location is still the Eurogate terminal in Hamburg.  The "only" new thing is that we
now have a `locationName`.  For `RTA-Berth` and other timestamps where `facilityTypeCode` is Berth (`BRTH`),
the `locationName` determines the Berth Location.  In this case, we are given `Waltershofer Hafen/S`, which
would translate to `Waltershofer Hafen` with the `/S` part likely being a reference to `Starboard` side.
The actual berth names come from the terminals so local conventions apply. _(For the concrete example, the
berth name was made up)_


As implied, the `locationName` has different meaning depending on the `facilityTypeCode`. When
`facilityTypeCode` is:

 * `BRTH` then `locationName` is the name of the berth.
 * `PBPL` (Pilot Boarding Place Location) then `locationName` is the name of the
   pilot boarding place location.
 * `ANCH` (Anchorage) then the `locationName` is the name of the anchor location.


# Now what?

Now that we have received the `RTA-Berth` and understood the details of it, we would have to consider
what to reply.  We can either reject it (and propose a new `ETA-Berth`) or accept it (by sending an
`PTA-Berth`).  Which option that works for you depends on your situation.

## Accepting the timestamp

If you accept, you could take the `RTA-Berth` as a baseline and perform the following changes:

 1. Update the `publisher` and `publisherRole` to match your `publisher` and `publisherRole`.
 2. Set the `eventClassifierCode` to `PLN` (making it a `PTA-Berth`).
 3. (Optionally)  Include an update of the distance to the port (the `milesToDestinationPort`).

Note it is important that you **keep** the `locationName` and `eventDateTime` as provided by the terminal
because you are arrival time and confirming the berth location.

## Rejecting the timestamp

If you reject the timestamp, you can just follow [the previous part of this module] for constructing
the `ETA-Berth`.  You would have to provide a new arrival time (`eventDateTime`) and `milesToDestinationPort`.

## Once you have a reply
Now that you have a reply, we can submit this to the terminal by `POST`'ing it to their JIT server
on the `/v1/timestamps` endpoint.

This concludes part of 2 of Module 9, and you now know how to interpret a timestamp that is a reply
to a timestamp you sent out.

You can find the [next part of this module here].

<!-- Links -->
[Module 4]: https://netorgft5018853.sharepoint.com/:v:/t/DCSAAssetProductivity-Vessel-InternalTeamDCSAonly/ETSfOTwaiy5CgM5TYerSyQgBTS48D_sRqxDANnb1F7Gu7g?e=qjTafP
[the previous part of this module]: Module%209%20part%201%20-%20preparing%20an%20ETA-Berth.md
[Swagger spec of JIT]: https://app.swaggerhub.com/apis/dcsaorg/DCSA_JIT/1.2.0-Beta-1
[next part of this module here]: Module%209%20part%203%20-%20classifying%20timestamps.md
