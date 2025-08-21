package com.amay.tom.api.service;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public interface IApiService {
    public HttpRequest createGetRequest(String url);

    public HttpRequest createPostRequest(String url, String body);

    public HttpRequest createPutRequest(String url, String body);

    public HttpRequest createDeleteRequest(String url);

    public void setHeaders(HttpRequest.Builder requestBuilder, String key, String value);

    public CompletableFuture<HttpResponse<String>> sendAsyncRequests(HttpRequest request);

    public HttpResponse<String> sendRequests(HttpRequest request);

}
