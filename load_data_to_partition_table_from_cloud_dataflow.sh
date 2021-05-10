#!/bin/bash

echo "Create Partition BigQuery table"
echo ""

export PROJECT=$1
export REGION=$2

gcloud dataflow jobs run data-streaming-dataflow-pipeline-partitioned \
--gcs-location gs://dataflow-templates-$REGION/latest/PubSub_to_BigQuery \
--region $REGION \
--max-workers 3 \
--num-workers 1 \
--staging-location gs://$PROJECT-temp/tmp \
--parameters inputTopic=projects/$PROJECT/topics/data-streaming-topic,outputTableSpec=$PROJECT:tsunami.telemetry_data_partitioned
echo "data-streaming-dataflow-pipeline created..."
echo ""