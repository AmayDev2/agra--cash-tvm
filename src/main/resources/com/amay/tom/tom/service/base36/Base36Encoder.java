package com.amay.tom.service.base36;

public class Base36Encoder {

    // Base36 character set: 0-9, a-z
    private static final String BASE36_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"; //oqi
    private static final int BASE = 36;

    /**
     * Encodes a decimal number into a Base36 string.
     * @param number A positive long number
     * @return Base36 encoded string
     */
    public static String encode(long number) {
        if (number < 0) throw new IllegalArgumentException("Number must be non-negative.");

        StringBuilder result = new StringBuilder();
        do {
            int remainder = (int) (number % BASE);
            result.append(BASE36_ALPHABET.charAt(remainder));
            number /= BASE;
        } while (number > 0);

        return result.reverse().toString();
    }

    /**
     * Decodes a Base36 string into a decimal number.
     * @param base36 Base36 encoded string (lowercase or uppercase)
     * @return Decoded long value
     */
    public static long decode(String base36) {
        if (base36 == null || base36.isEmpty()) throw new IllegalArgumentException("Input cannot be null or empty.");

        long result = 0;
        for (int i = 0; i < base36.length(); i++) {
            char c = base36.charAt(i);
            int value = BASE36_ALPHABET.indexOf(c);
            if (value == -1) throw new IllegalArgumentException("Invalid character in Base36 string: " + c);
            result = result * BASE + value;
        }

        return result;
    }




}
