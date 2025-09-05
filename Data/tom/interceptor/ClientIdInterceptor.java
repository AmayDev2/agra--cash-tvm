package com.amay.tom.interceptor;

import io.grpc.*;

public class ClientIdInterceptor implements ClientInterceptor {

    private final String clientId;

    public ClientIdInterceptor(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {

        // Create new metadata and add the clientId
        Metadata headers = new Metadata();
        Metadata.Key<String> clientIdKey = Metadata.Key.of("ClientId", Metadata.ASCII_STRING_MARSHALLER);
        headers.put(clientIdKey, clientId);

        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
                next.newCall(method, callOptions)) {

            @Override
            public void start(Listener<RespT> responseListener, Metadata metadata) {
                // Merge clientId headers with existing headers
                metadata.merge(headers);
                super.start(responseListener, metadata);
            }
        };
    }
}
