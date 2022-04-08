package com.usersApi.Users.Configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "com.usersapi")
public class BigQueryAppProperties {
    private String credentialsPath = "/apiusers-346001-98cd3efe9bbc.json";
    private String projectId = "apiusers-346001";

}