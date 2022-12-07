#!/usr/bin/python3

import collections
import csv
import os
import sys


KEEP_FIELDS = [
  'testID',
  'testType',
  'timestampTypeName',
  'eventClassifierCode',
  'operationsEventTypeCode',
  'portCallPhaseTypeCode',
  'portCallServiceTypeCode',
  'facilityTypeCode',
  'includeEventLocationName',
  'isTerminalNeeded',
  'includeVesselDraft',
  'includeVesselPosition',
  'includeMilesToDestination',
  'publisherRole',
  'expectedHTTPCode',
  'errorExpectedReason',
]

RequirementToInclude = collections.namedtuple('RequirementToInclude', ['for_positive_tests', 'for_negative_tests'])

REQUIREMENT_TO_BOOLS = {
  'REQUIRED': RequirementToInclude(for_positive_tests=['true'], for_negative_tests=['false']),
  'OPTIONAL': RequirementToInclude(for_positive_tests=['false', 'true'], for_negative_tests=[]),
  'EXCLUDED': RequirementToInclude(for_positive_tests=['false'], for_negative_tests=['true']),
}


def join_csv(table_a, column_a, table_b, column_b=None):
    if column_b is None:
        column_b = column_a
    join_dict = collections.defaultdict(list)
    for row in table_b:
        join_dict[row[column_b]].append(row)

    for row in table_a:
        for match in join_dict[row[column_a]]:
            result_row = row.copy()
            for k, v in match.items():
                if k in result_row and result_row[k] != v:
                    raise ValueError(f"Conflicting definition of {k} when joining on {column_a} and {column_b}")
                result_row[k] = v
            yield result_row


def read_csv(filename):
    with open(filename) as fd:
        csv_reader = csv.DictReader(fd)
        yield from csv_reader


def expand_optional_bool(table, old_name, new_name):
    for row in table:
        row = row.copy()
        value = row[old_name]
        # For tests that are already negative, we do not vary this field.
        if row['testType'] != 'positive':
            value = 'false'
        row[new_name] = value
        del row[old_name]
        yield row
        # If True, we expand to also include a row the bool set to False
        # for positive tests
        if row[new_name].lower() == 'true' or row['testType'] != 'positive':
            continue
        row = row.copy()
        row[new_name] = 'false'
        yield row


def expand_xor_bool(table, column_name):
    for row in table:
        # normalize case
        row[column_name] = row[column_name].lower()
        yield row
        # For tests that are already negative, we do not vary this field.
        if row['testType'] != 'positive':
            continue
        row = row.copy()
        row[column_name] = 'false' if row[column_name].lower() == 'true' else 'true'
        mark_as_negative_test(row)
        yield row


def expand_requirement_column_into_bool_field(table, old_name, new_name):
    for row in table:
        r2b = REQUIREMENT_TO_BOOLS[row[old_name]]
        if row['testType'] != 'positive':
            # If it is already a negative test, we just pick the first valid
            # option for this row and move on (to avoid "contaminating" the
            # test with additional failures)
            result = row.copy()
            del result[old_name]
            result[new_name] = r2b.for_positive_tests[0]
            yield result
            continue
        # First, we generate positive tests
        for bool_value in r2b.for_positive_tests:
            result = row.copy()
            del result[old_name]
            result[new_name] = bool_value
            yield result
        # Then we generate negative tests based on this positive sample
        for bool_value in r2b.for_negative_tests:
            result = row.copy()
            del result[old_name]
            result[new_name] = bool_value
            mark_as_negative_test(result)
            yield result


def mark_as_negative_test(row):
    row['testType'] = 'negative'
    row['expectedHTTPCode'] = '400'
    row['errorExpectedReason'] = 'invalidInput'


def rename_column(table, old_name, new_name):
    for row in table:
        row = row.copy()
        row[new_name] = row[old_name]
        del row[old_name]
        yield row


def write_test_csv(filename, timestamps):
    with open(filename, 'w', newline='') as fd:
        writer = csv.DictWriter(fd, fieldnames=KEEP_FIELDS)
        writer.writeheader()
        for id, timestamp in enumerate(timestamps, start=1):
            pruned_row = {field: timestamp[field] for field in KEEP_FIELDS if field != 'testID'}
            pruned_row['testID'] = id
            writer.writerow(pruned_row)


def trim_and_deduplicate(table, *fields):
    seen = set()
    for row in table:
        seen_key = tuple(row[field] for field in fields)
        if seen_key in seen:
            continue
        seen.add(seen_key)
        yield {field: row[field] for field in fields}


def mark_all_as_positive_test(table):
    for row in table:
        row['testType'] = 'positive'
        row['expectedHTTPCode'] = '204'
        row['errorExpectedReason'] = 'null'
        yield row


def build_data_sets(jit_root_dir, jit_test_data_path):
    data_dir = os.path.join(jit_root_dir, 'DCSA-Information-Model', 'datamodel', 'implementation-detail-data.d')
    if not os.path.isdir(data_dir):
        print(f"Missing {data_dir}: Ensure the DCSA-Information-Model git submodule has been setup correctly")
        sys.exit(1)
    timestamp_defs_filename = os.path.join(data_dir, 'timestampdefinitions.csv')
    ts_def_pp_join_table_filename = os.path.join(data_dir, 'timestampdefinitions_publisherpattern.csv')
    publisher_pattern_filename = os.path.join(data_dir, 'publisherpattern.csv')
    publisher_patterns = read_csv(publisher_pattern_filename)
    join_table = read_csv(ts_def_pp_join_table_filename)
    publisher_patterns_merged = join_csv(publisher_patterns, "Pattern ID", join_table)
    # Discard some unnecessary columns, which will also enable us to discard a lot of rows
    # This deduplication avoids unnecessary duplicated tests in the resulting output, so it
    # is primarily for correctness (as we do not want to test "RTA-Berth" with the "TR"
    # publisher role multiple times just because there mulitple options for the primary
    # receiver
    publisher_patterns_merged = trim_and_deduplicate(publisher_patterns_merged, "Timestamp ID", "Publisher Role")
    timestamp_defs = read_csv(timestamp_defs_filename)
    timestamps_with_publisher_role = join_csv(timestamp_defs, "timestampID", publisher_patterns_merged, "Timestamp ID")
    timestamps_with_publisher_role = mark_all_as_positive_test(timestamps_with_publisher_role)
    # Rename column to make the field easier to consume in postman/newman
    timestamps_with_publisher_role = rename_column(timestamps_with_publisher_role, "Publisher Role", "publisherRole")

    timestamps_with_publisher_role = expand_requirement_column_into_bool_field(
      timestamps_with_publisher_role,
      'eventLocationRequirement',
      'includeEventLocationName'
    )
    timestamps_with_publisher_role = expand_requirement_column_into_bool_field(
      timestamps_with_publisher_role,
      'vesselPositionRequirement',
      'includeVesselPosition'
    )
    timestamps_with_publisher_role = expand_optional_bool(
      timestamps_with_publisher_role,
      'isVesselDraftRelevant',
      'includeVesselDraft'
    )
    timestamps_with_publisher_role = expand_optional_bool(
      timestamps_with_publisher_role,
      'isMilesToDestinationRelevant',
      'includeMilesToDestination'
    )
    timestamps_with_publisher_role = expand_xor_bool(timestamps_with_publisher_role, "isTerminalNeeded")
    write_test_csv(jit_test_data_path, timestamps_with_publisher_role)


def usage():
    print("Usage: python3 " + os.path.basename(sys.argv[0]) + ".py test-out-file.csv [path/to/DCSA-JIT]")
    sys.exit(1)


def main():
    if len(sys.argv) > 3 or len(sys.argv) < 2:
        usage()
    jit_test_out = sys.argv[1]
    if len(sys.argv) >= 3:
        jit_root_dir = sys.argv[2]
    else:
        jit_root_dir = os.path.dirname(os.path.realpath(sys.argv[0]))
    build_data_sets(jit_root_dir, jit_test_out)


if __name__ == '__main__':
    main()
