package com.amay.tom.exceptions;

import org.tinylog.Logger;

public class DecodingBase64Exception extends RuntimeException {

        public DecodingBase64Exception(String message) {
            super(message);
            Logger.error("Error decoding Base64 string : " + message);
        }

        public DecodingBase64Exception(String message, Throwable cause) {
            super(message, cause);

            Logger.error("Error decoding Base64 string : " + message);
        }

        public DecodingBase64Exception(Throwable cause) {
            super(cause);
        }

        public DecodingBase64Exception() {
            super();
        }
}
