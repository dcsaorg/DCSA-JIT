# Module 9 part 3 - classifying timestamps

This is part 3 of Module 9 of the DCSA JIT Adoption pack.
The document is written for `JIT 1.2.0-beta1`.

This document will cover a quick guide on translating between the high
level `ETA-Berth` and the low level technical specification
of the DCSA JIT specification.

The reader is assumed to:

 * Be familiar with JSON
 * Understand the "Estimated -> Requested -> Planned" (`E -> R -> P`)
   negotiation cycle covered in Module 4.

This part of the module _can_ be read without reading the previous
parts. However, we still recommend you have read them first.

## Context of this document

In this document, we will zoom in on a very small part of the
technical timestamp.  This document will *not* cover process
or the full technical details of the timestamp at any noteworthy
length.

# Getting started.

In the previous parts of this module, we have taken a blob of JSON and
asserted that "this is an `ETA-Berth` and that is an `RTA-Berth`".  In
this part, we will dive into how to do this translation, so you can perform
it yourself.

To start out, let us start by grouping timestamps into the following broad categories:

 1. Timestamps that follow the `XTY-Z` pattern.  This is vast majority of timestamps
    and includes timestamps such as `ETA-Berth`, `RTA-PBP`, `PTS-Cargo Ops`,
    or `ATC-Cargo Ops`.
 2. Timestamps that do not follow the "logical" pattern above.  These include
    `AT All Fast`, `EOSP` (End Of Sea Passage), `Vessel Readiness for cargo operations`
    - For these, the best solution is to look them up in the IFS to see the proper
      combination of values to classify these timestamps.  DCSA also has a CSV file
      with [timestamp definitions] that you can use.

[timestamp definitions]: ../DCSA-Information-Model/datamodel/samples.d/timestampdefinitions.csv

The first group is most common and also easiest to understand, so we will begin with
those.

# The `eventClassifierCode` attribute

To start off, the let us pull up the JSON properties relevant for classifying
the timestamp.


```json
{
  "eventClassifierCode": "EST",
  "operationsEventTypeCode": "ARRI",
  "facilityTypeCode": "BRTH",
  "portCallPhaseTypeCode": null,
  "portCallServiceTypeCode": null
}
```

If you remember part 1, you might remember this combination being used in the `ETA-Berth`
timestamp. For now, we will start with understanding the `eventClassifierCode`. In the
example, it is set to `EST` and it makes up the `E` in the `ETA` of `ETA-Berth`.  The
`eventClassifierCode` have the following possibly values:

 * `EST` - estimated (as in `ETA-Berth`)
 * `REQ` - requested (as in `RTA-Berth`)
 * `PLN` - planned (as in `PTA-Berth`)
 * `ACT` - actual (as in `ATA-Berth`)

As you might be able to tell, the `eventClassifierCode` directly controls the initial letter
of the timestamps that follow the nice patterns. In fact, _the_ field that is used to control
the `E -> R -> P` negotiation cycle.  Therefore, with this knowledge, you can now transform
`ETA-Berth` into:

 * `RTA-Berth`
 * `PTA-Berth`
 * `ATA-Berth`

As an example, here is how `PTA-Berth` would be represented:

```json
{
  "eventClassifierCode": "PLN",
  "operationsEventTypeCode": "ARRI",
  "facilityTypeCode": "BRTH",
  "portCallPhaseTypeCode": null,
  "portCallServiceTypeCode": null
}
```

Note that only the `eventClassifierCode` changed.

# The `operationsEventTypeCode` attribute
Similarly to the `eventClassifierCode`, the `operationsEventTypeCode` attribute makes up the `A`
from `ETA`. For timestamps that follow the pattern, it has a similar effect on that letter.  The
`operationsEventTypeCode` has the following possible values:

 * `ARRI` - arrival (as in `ETA-Berth`)
 * `DEPA` - departure (as in `ETD-Berth`)
 * `STRT` - start/started (as in `ETS-Cargo Ops`)
 * `CMPL` - completion/completed (as in `ETC-Cargo Ops`)
 * `CANC` - cancel (`Cancel Bunkering` - timestamps with this value do not follow the "nice pattern")
 * `OMIT` - Omitting (`OMIT Port Call` - the timestamp with this value does not follow the "nice pattern")

With `operationsEventTypeCode` (and `eventClassifierCode` from above), you can now take the above
`ETA-Berth` and transform it into one of the following:

 * `ETD-Berth`
 * `RTD-Berth`
 * `PTD-Berth`
 * `ATD-Berth`

To conclude this subsection with a concrete example, here is a `ETD-Berth`:

```json
{
  "eventClassifierCode": "EST",
  "operationsEventTypeCode": "DEPA",
  "facilityTypeCode": "BRTH",
  "portCallPhaseTypeCode": null,
  "portCallServiceTypeCode": null
}
```

Note that only the `operationsEventTypeCode` changed compared to the `ETA-Berth`.

# The `facilityTypeCode` attribute

The `facilityTypeCode` defines where something happens (at what kind of location).  For
timestamps that follow the pattern, this attribute also contributes to the name of the
timestamp *if* the `portCallServiceTypeCode` is null (or omitted).


The `facilityTypeCode` can take the following values:

 * `BRTH` - Berth (`ETA-Berth`)
 * `PBPL` - Pilot Boarding Place Location (`ETA-PBP`)
 * `ANCH` - Anchorage (`ETA-Anchorage`)

For pattern timestamp or when `portCallServiceTypeCode` is not null, `facilityTypeCode` is
always a not null value. But `facilityTypeCode` can be null in some cases (e.g., `EOSP`).

When there is a `portCallServiceTypeCode`, then `facilityTypeCode` is _often_ `BRTH` as
_most_ services only happen at berth. However, a few services such as Bunkering or Sludge,
which _can_ happen at anchorage (`facilityTypeCode` with value of `ANCH`).  Additionally,
some timestamps (based on the `portCallPhaseTypeCode`) can happen at Pilot Boarding
Place (`facilityTypeCode` with value of `PBPL`).

As noted in the previous parts, the `facilityTypeCode` also affects the `Event Location Info`
fields.  When `facilityTypeCode` is:

 * `BRTH` then the location must include a terminal (see the attributes starting with `facility`).
   If a `locationName` is provided, it defines the name of the berth.
 * `PBPL` then the location must *not* include a terminal.  The `locationName` then refers to
   the pilot boarding place.
 * `ANCH` then the location must *not* include a terminal.  The `locationName` then refers to
   the anchorage location.

With the addition of the `facilityTypeCode`, you can now represent the following extra
timestamps:

 * `ETA-PBP`
 * `RTA-PBP`
 * `PTA-PBP`
 * `ATA-PBP`
 * `ETA-Anchorage`
 * `RTA-Anchorage`
 * `PTA-Anchorage`
 * `ATA-Anchorage`


To conclude this subsection with a concrete example, here is a `ETA-PBP`:

```json
{
  "eventClassifierCode": "EST",
  "operationsEventTypeCode": "ARRI",
  "facilityTypeCode": "PBPL",
  "portCallPhaseTypeCode": null,
  "portCallServiceTypeCode": null
}
```

Note that only the `facilityTypeCode` changed compared to the `ETA-Berth`.

# The `portCallServiceTypeCode`  attribute

The `portCallServiceTypeCode` defines which service the timestamp is about. When the timestamp
follows the nice pattern, then the `portCallServiceTypeCode` also contributes the name of the
timestamp.

It is worth noting that services use `STRT` (Start) / `CMPL` (Complete) rather than `ARRI`
(Arrival) / `DEPA` (Departure) in the `operationsEventTypeCode`.  This also means that all the
service related timestamps that follow the "nice pattern" use `S` or `C` in their third letter
(such as `ETS-Cargo Ops`).

The `portCallServiceTypeCode` has a long list of values, this guide will not cover all them.
Instead, please review the Swagger specification or the IFS for the full list.  For now, here
is an incomplete list for the sake of the example:

 * `CRGO` - Cargo Ops (`ETS-Cargo Ops`)
 * `BUNK` - Bunkering (`ETS-Bunkering`)
 * `SLUG` - Sludge (`ETS-Sludge`)

The above here do not require `portCallPhaseTypeCode` and with these you can now start to specify
the following timestamps:

 * `ETS-Cargo Ops`
 * `RTS-Cargo Ops`
 * `PTS-Cargo Ops`
 * `ATS-Cargo Ops`
 * `ETC-Cargo Ops`
 * `RTC-Cargo Ops`
 * `PTC-Cargo Ops`
 * `ATC-Cargo Ops`

Rinse and repeat for the other service types listed above.  As an example, here is a `RTC-Cargo Ops`:

```json
{
  "eventClassifierCode": "EST",
  "operationsEventTypeCode": "CMPL",
  "facilityTypeCode": "PBPL",
  "portCallPhaseTypeCode": "CRGO",
  "portCallServiceTypeCode": null
}
```

Note that we changed `portCallPhaseTypeCode` and `operationsEventTypeCode` compared to the `ETA-Berth`.

For some services, you also need a `portCallPhaseTypeCode` as they happen multiple times during a visit.
These include:

 * `PILO` - Pilotage (`ETS-Pilotage (Inbound)` vs `ETS-Pilotage (Outbound)`)
 * `TOWG` - Towage (`ETS-Towage (Inbound)` vs `ETS-Towage (Outbound)`)

We will cover them in the next section along with the `portCallPhaseTypeCode` attribute.

# The `portCallPhaseTypeCode` attribute

The `portCallPhaseTypeCode` attribute describes at what "phase" something is happening. To start with,
the `portCallPhaseTypeCode` has 4 values:

 * `INBD` - Inbound - Arriving at the port or terminal (`ETS-Pilotage (Inbound)`)
 * `OUTD` - Outbound - Departing from the port or terminal (`ETS-Pilotage (Outbound)`)
 * `SHIF` - Shifting - Moving from one terminal in the port to another terminal (`ETS-Pilotage (Shifting)`)
 * `ALGS` - Alongside - At a berth (No good example timestamp)

As mentioned under the `portCallServiceTypeCode` section, some services such as Pilotage can happen in
multiple phases. The vessel might need Pilotage when arriving, when moving from one terminal to another
or when departing from the port.  We use the `portCallPhaseTypeCode` attribute to tell these cases apart.


As an example, here are two ETC-Pilotage timestamps.  The first one is for arrival and the other one is
for shifting.

**ETC-Pilotage (Inbound)**:
```json
{
  "eventClassifierCode": "EST",
  "operationsEventTypeCode": "CMPL",
  "facilityTypeCode": "BRTH",
  "portCallPhaseTypeCode": "PILO",
  "portCallServiceTypeCode": "INBD"
}
```

**ETC-Pilotage (Shifting)**:
```json
{
  "eventClassifierCode": "EST",
  "operationsEventTypeCode": "CMPL",
  "facilityTypeCode": "BRTH",
  "portCallPhaseTypeCode": "PILO",
  "portCallServiceTypeCode": "SHIF"
}
```

In this "nicely chosen" example, only the `portCallPhaseTypeCode` changes. However, the `portCallPhaseTypeCode`
tends to have non-trivial effects on the timestamp.  As an example, the Pilotage and Towage timestamps have
different values for `facilityTypeCode` depending on `operationsEventTypeCode` and the `portCallPhaseTypeCode`.
You are recommended to use the IFS or the [timestamp definitions] CSV file when working with timestamps that
requires `portCallPhaseTypeCode`.

For timestamps, where `portCallPhaseTypeCode` is not a necessity. You are recommended to omit the
`portCallPhaseTypeCode` entirely (or set it to `null`).  To understand the rationale behind this
recommendation, please consider `ETA-Berth` and `ATA-Berth` - neither of which requires the
`portCallPhaseTypeCode`.  If you do provide `portCallPhaseTypeCode`, then it will be `INBD`
for `ETA-Berth` while being `ALGS` for `ATA-Berth`.  Similar holds for departure where the
`ATD-Berth` has a different `portCallPhaseTypeCode` from the `E -> R -> P` version of the
timestamp.  It is to avoid these small "surprises" that we recommend that you start by omitting
the `portCallPhaseTypeCode` for timestamps where it can be omitted without any loss of understanding.

## Backwards compatibility and `portCallPhaseTypeCode`

The first JIT version (1.0) defined a Pilotage timestamp (e.g., `ETS-Pilotage`) but did not have a
`portCallPhaseTypeCode`.  This means that JIT 1.0 clients will omit the `portCallPhaseTypeCode` in
their message.  For the Pilotage timestamp, JIT 1.0 defined the Inbound version and JIT 1.1+ or later
implementations should interpret Pilotage timestamps without `portCallPhaseTypeCode` as if
`portCallPhaseTypeCode` was `INBD`.

For other JIT 1.0 timestamps, the omission of the `portCallPhaseTypeCode` will not create ambiguity.

# Conclusion
This concludes part 3 of Module 9, and you now have a basic understanding of how you translate between
high level timestamp and technical level timestamp.

This is the end of Module 9.
