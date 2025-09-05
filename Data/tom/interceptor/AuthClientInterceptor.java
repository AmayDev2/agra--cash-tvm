package com.amay.tom.interceptor;

import io.grpc.*;

public class  AuthClientInterceptor implements ClientInterceptor {

    private final String token;

    public AuthClientInterceptor(String token) {
        this.token = token;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {

        // Create new metadata and add the token
        Metadata headers = new Metadata();
        Metadata.Key<String> tokenKey = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
        headers.put(tokenKey, "Bearer " + token);

        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
                next.newCall(method, callOptions)) {

            @Override
            public void start(Listener<RespT> responseListener, Metadata metadata) {
                // Merge token headers with existing headers
                metadata.merge(headers);
                super.start(responseListener, metadata);
            }
        };
    }
}
