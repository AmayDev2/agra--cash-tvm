package com.amay.tom.interceptor;

import io.grpc.*;

public class ClientLoggingInterceptor implements ClientInterceptor {

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {

        //System.out.println("Calling method: " + method.getFullMethodName());

        // Proceed with the call
        return next.newCall(method, callOptions);
    }
}
