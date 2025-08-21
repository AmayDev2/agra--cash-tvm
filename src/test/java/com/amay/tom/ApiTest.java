//package com.amay.tom;
//
//import com.amay.tom.api.ApiClient;
//import com.amay.tom.api.service.IApiRequest;
//import com.amay.tom.api.service.IApiService;
//import com.amay.tom.api.service.impl.ApiRequest;
//import com.amay.tom.api.service.impl.ApiService;
//import org.junit.jupiter.api.Test;
//
//import java.net.http.HttpRequest;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//public class ApiTest {
//
//
//
//    @Test
//    void testMain() {
//        ApiClient apiClient = new ApiClient();
//        assertNotNull(apiClient);
//    }
//
//    @Test
//    void testApiService() {
//        ApiClient apiClient = new ApiClient();
//        IApiRequest apiRequest = new ApiRequest();
//        apiRequest.createGetRequest( "https://jsonplaceholder.typicode.com/todos");
//        apiRequest.setHeader("Content-Type", "application/json");
//        apiClient.sendAsyncRequest(apiRequest.buildRequest()).thenApply(response -> {
//            System.out.println("Response: " + response.body());
//            return response;
//        }).join();
//        System.out.println("******************************************");
//
//    }
//}
