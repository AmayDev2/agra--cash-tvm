package com.amay.tom.api.service;

import java.net.http.HttpRequest;
import java.util.HashMap;

public interface IApiRequest {

    public void createGetRequest(String url) ;

    public void setHeader( String key, String value);

    public void setHeaders( HashMap<String, String> headers);

    public void buildGetRequest(HttpRequest.Builder reqBuilder) ;

    public void createPostRequest(String url, String body);

    public HttpRequest buildRequest() ;
}
