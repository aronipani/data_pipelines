import com.google.cloud.bigquery.*;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Sample to extract a compressed table
public class LoadPTblfromGCScsv implements HttpFunction {

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime now = LocalDateTime.now();
        //String fileName = ("telemetry_" + dtf.format(now)+".gzip");
        String fileName = ("telemetry_" + dtf.format(now)+".csv");

        String projectName = "manning-data-pipelines";
        String datasetName = "tsunami";
        String tableName = "telemetry_data";
        String bucketName = "manning-data-pipeline-archive-storage";
        String destinationUri = "gs://" + bucketName + "/" + fileName;
        String compressed = "gzip";
        String dataFormat = "CSV";

        extractTableCompressed(
                projectName, datasetName, tableName, destinationUri, dataFormat, compressed);
    }

    public static void extractTableCompressed(
            String projectName,
            String datasetName,
            String tableName,
            String destinationUri,
            String dataFormat,
            String compressed) {
        try {
            //String jsonPath = "/Users/user/repo/Manning/GCPSvcCredentials/manning-data-pipelines-082a4f59df18.json";
            //GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(jsonPath))
            //       .createScoped(Arrays.asList("https://www.googleapis.com/auth/cloud-platform"));
            //Context.Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
            //BigQuery bigquery = BigQueryOptions.newBuilder().setCredentials(credentials).build().getService();
            BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();

            TableId tableId = TableId.of(projectName, datasetName, tableName);
            /*
            ExtractJobConfiguration extractConfig =
                    ExtractJobConfiguration.newBuilder(tableId, destinationUri)
                            .setCompression(compressed)
                            .setFormat(dataFormat)
                            .build();

             */
            ExtractJobConfiguration extractConfig =
                    ExtractJobConfiguration.newBuilder(tableId, destinationUri)
                            .setFormat(dataFormat)
                            .build();

            Job job = bigquery.create(JobInfo.of(extractConfig));

            // Blocks until this job completes its execution, either failing or succeeding.
            Job completedJob = job.waitFor();
            if (completedJob == null) {
                System.out.println("Job not executed since it no longer exists.");
                return;
            } else if (completedJob.getStatus().getError() != null) {
                System.out.println(
                        "BigQuery was unable to extract due to an error: \n" + job.getStatus().getError());
                return;
            }
            System.out.println("Table extract compressed successful");
        } catch (BigQueryException | InterruptedException  e) {
            System.out.println("Table extraction job was interrupted. \n" + e.toString());
        }
    }


}