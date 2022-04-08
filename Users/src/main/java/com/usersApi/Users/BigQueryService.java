package com.usersApi.Users;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryError;
import com.google.cloud.bigquery.BigQueryException;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.InsertAllRequest;
import com.google.cloud.bigquery.InsertAllResponse;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobId;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.JobStatus;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableResult;
import com.usersApi.Users.Configuration.BigQueryAppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@EnableConfigurationProperties({BigQueryAppProperties.class})
public class BigQueryService {

    private final BigQueryAppProperties properties;

    public BigQueryService(BigQueryAppProperties properties){
        this.properties = properties;
    }

    private GoogleCredentials getCredentials(String fileName) throws IOException {
        InputStream in = getClass().getResourceAsStream(fileName);
        return ServiceAccountCredentials.fromStream(in);
    }

    public void addUser(final int document, final String documentType, final String firstName, final String lastName, final String email, final String gender) throws Exception {
        String datasetName = "Userssample";
        String tableName = "users";

        BigQuery query;
        try{
            query = BigQueryOptions.newBuilder().setProjectId(properties.getProjectId()).setCredentials(getCredentials(properties.getCredentialsPath())).build().getService();
        }catch(Exception ex){
            throw new Exception("Can't find credentials");
        }
        Map<String, Object> rowContent = new HashMap<>();
        rowContent.put("document", document);
        rowContent.put("documentType", documentType);
        rowContent.put("first_name", firstName);
        rowContent.put("last_name", lastName);
        rowContent.put("email", email);
        rowContent.put("gender", gender);

        try{
            TableId tableId = TableId.of(datasetName, tableName);
            InsertAllResponse response =
                    query.insertAll(
                            InsertAllRequest.newBuilder(tableId)
                                    .addRow(rowContent)
                                    .build());
            if (response.hasErrors()) {
                for (Map.Entry<Long, List<BigQueryError>> entry : response.getInsertErrors().entrySet()) {
                    System.out.println("Response error: \n" + entry.getValue());
                }
            }
        }catch (BigQueryException e) {
            System.out.println("Insert operation not performed \n" + e.toString());
        }


    }

    public List<String> getUsers(String... args) throws Exception{
        int document = args.length > 0 ?  Integer.parseInt(args[0]) : 0;
        String documentType = args.length > 1 ? args[1] : "";

        BigQuery query;
        try{
            query = BigQueryOptions.newBuilder().setProjectId(properties.getProjectId()).setCredentials(getCredentials(properties.getCredentialsPath())).build().getService();
        }catch(Exception ex){
            throw new Exception("Can't find credentials");
        }

        String condition = "SELECT TO_JSON_STRING(users) AS data From apiusers-346001.Userssample.users as users";

        if(document != 0){
            condition += (" Where document = "+document+"");
            if(!documentType.isEmpty()) {
                condition += (" And documentType = '" + documentType + "'");
            }
        } else if(!documentType.isEmpty()){
            condition += (" Where documentType = '"+documentType+"'");
        }

        final QueryJobConfiguration queryConfig =
                QueryJobConfiguration.newBuilder(condition)
                        .build();
        JobId jobId = JobId.of(String.valueOf(UUID.randomUUID()));
        Job queryJob = query.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build());
        try {
            queryJob = queryJob.waitFor();
            JobStatus jobStatus = queryJob.getStatus();

            if (queryJob == null) {
                throw new Exception("Job not found");
            } else if (jobStatus.getError() != null) {
                throw new Exception(jobStatus.getError().toString());
            }

            final TableResult result = queryJob.getQueryResults();

            final List<String> fieldList = result.getSchema().getFields().stream()
                    .map(Field::getName)
                    .collect(Collectors.toList());

            List<String> data = new ArrayList<>();
            for (FieldValueList row : result.iterateAll()) {
                fieldList.forEach(field -> data.add(row.get(field).getStringValue()));
            }
            return data;

        }catch (Exception ex){
            throw new Exception("Query Failed");
        }

    }


}
