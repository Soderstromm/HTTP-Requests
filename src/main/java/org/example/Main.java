package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {

    private static final String REMOTE_SERVICE_URI = "https://raw.githubusercontent.com/netology-code/jd-homeworks/master/http/task1/cats";

    public static void main(String[] args) {

        try {
            try (CloseableHttpClient httpClient = HttpClientBuilder.create()
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                            .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                            .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                            .build())
                    .build()) {

                HttpGet request = new HttpGet(REMOTE_SERVICE_URI);

                request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode != 200) {
                        System.err.println("Ошибка HTTP: " + statusCode);
                        return;
                    }

                    String json = EntityUtils.toString(response.getEntity());

                    Post[] facts = parseJson(json);

                    filterFacts(facts).forEach(System.out::println);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static Post[] parseJson(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, Post[].class);
    }

    private static List<Post> filterFacts(Post[] facts) {
        return Arrays.stream(facts)
                .filter(fact -> fact.getUpvotes() != null && fact.getUpvotes() > 0)
                .toList();
    }
}