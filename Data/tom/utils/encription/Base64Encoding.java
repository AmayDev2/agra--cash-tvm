package com.amay.tom.utils.encription;

import com.amay.tom.exceptions.DecodingBase64Exception;

import java.util.Base64;

public class Base64Encoding {

    public static String encode(String originalString){
        // Encode a string to Base64
        String encodedString = Base64.getEncoder().encodeToString(originalString.getBytes());
        System.out.println("Encoded string: " + encodedString);
        return encodedString;
    }

    public static String decode( String encodedString) {
        // Decode a string from Base64
        try {
            return new String(Base64.getDecoder().decode(encodedString));
        } catch (IllegalArgumentException e) {
            throw new DecodingBase64Exception("Error decoding Base64 string : "+ encodedString, e);
        }
    }
}
