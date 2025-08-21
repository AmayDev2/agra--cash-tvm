package com.amay.tom.api.service.impl;

import com.amay.tom.api.ApiClient;
import com.amay.tom.api.service.IApiService;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class ApiService implements IApiService {
    private ApiClient apiClient;


    public ApiService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

   public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public HttpRequest createGetRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
    }


    public HttpRequest createPostRequest(String url, String body) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    public HttpRequest createPutRequest(String url, String body) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    public HttpRequest createDeleteRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .build();
    }

public void setHeaders(HttpRequest.Builder requestBuilder, String key, String value) {
        requestBuilder.header(key, value);
    }

    public CompletableFuture<HttpResponse<String>> sendAsyncRequests(HttpRequest request)  {
        return apiClient.getHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> sendRequests(HttpRequest request)  {
        try {
            return apiClient.getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }



}
