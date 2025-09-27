package com.amay.tom.api;

import com.amay.tvm.backend.enums.LoggerTag;
import org.tinylog.Logger;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class ApiClient {

    private HttpClient httpClient;

    public ApiClient() {
        this.httpClient = HttpClient.newBuilder().build();
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void close() {
        httpClient = null;
    }

    public void open() {
        httpClient = HttpClient.newHttpClient();
    }

    public void setConnectionTimeout(int timeout) {
        httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(timeout)).build();
    }

    public void setReadTimeout(int timeout) {
        httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(timeout)).build();
    }

    public void setFollowRedirects(boolean followRedirects) {
//        httpClient = HttpClient.newBuilder().followRedirects().build();
    }

    public void setVersion(HttpClient.Version version) {
        httpClient = HttpClient.newBuilder().version(version).build();
    }

    public void setCookieHandler() {
        httpClient = HttpClient.newBuilder().cookieHandler(null).build();
    }


    public void setProxy() {
        httpClient = HttpClient.newBuilder().proxy(null).build();
    }


    public void setAuthenticator() {
        httpClient = HttpClient.newBuilder().authenticator(null).build();
    }

    public void setExecutor() {
        httpClient = HttpClient.newBuilder().executor(null).build();
    }

    public void setSslContext() {
        httpClient = HttpClient.newBuilder().sslContext(null).build();
    }

    public void setSslParameters() {
        httpClient = HttpClient.newBuilder().sslParameters(null).build();
    }

    public void setPriority() {
        httpClient = HttpClient.newBuilder().priority(1).build();
    }

    public String sendRequest(HttpRequest request) {

        try {

            return this.httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException | InterruptedException e) {
            Logger.tag(LoggerTag.APP).error(e.getMessage());
        }
        return null;
    }



    public CompletableFuture<HttpResponse<String>> sendAsyncRequest(HttpRequest request) {

        return this.httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }






}
