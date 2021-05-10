#!/bin/bash

echo "Create Partition BigQuery table"
echo ""

export PROJECT=$1
export REGION=$2

#Create a BigQuery Dataset and an empty table
bq mk --location $REGION $PROJECT:tsunami
bq mk --table \
  --schema timestamp:TIMESTAMP,tsunami_event_validity:INTEGER,tsunami_cause_code:INTEGER,earthquake_magnitude:FLOAT,latitude:FLOAT,longitude:FLOAT,maximum_water_height:FLOAT \
  --time_partitioning_field timestamp \
  --time_partitioning_type DAY \
  manning-data-pipelines:tsunami.telemetry_data_p1

echo "tsunami partition table created..."
echo ""