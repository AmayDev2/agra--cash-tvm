package com.amay.tom.api.service.impl;

import com.amay.tom.api.service.IApiRequest;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;

public class ApiRequest implements IApiRequest {
    
    private final HttpRequest.Builder reqBuilder;
    Map<String,String> headers;

    public ApiRequest() {
        this.reqBuilder = HttpRequest.newBuilder();
        setHeaders((HashMap<String, String>) headers);
    }

    {
        headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
    }

    public void createGetRequest(String url) {
        this.reqBuilder.uri(URI.create(url));
    }

    public void setHeader( String key, String value) {
        this.reqBuilder.header(key, value);
    }

    @Override
    public void setHeaders( HashMap<String, String> headers) {
        for (String key : headers.keySet()) {
            this.reqBuilder.header(key, headers.get(key));
        }
    }


    public void buildGetRequest(HttpRequest.Builder reqBuilder) {
        this.reqBuilder.GET();
    }

    public void createPostRequest(String url, String body) {
        this.reqBuilder.uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(body));
    }
    
    public HttpRequest buildRequest() {
        return this.reqBuilder.build();
    }
}
