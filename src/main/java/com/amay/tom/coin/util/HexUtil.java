package com.amay.tom.coin.util;

public final class HexUtil {
	private static final char[] HEX = "0123456789ABCDEF".toCharArray();

	private HexUtil() {}

	public static String toHex(byte value) {
		int v = value & 0xFF;
		return new String(new char[] { HEX[v >>> 4], HEX[v & 0x0F] });
	}

	public static String toHex(byte[] data) {
		if (data == null) return "null";
		StringBuilder sb = new StringBuilder(data.length * 2);
		for (byte b : data) {
			int v = b & 0xFF;
			sb.append(HEX[v >>> 4]).append(HEX[v & 0x0F]);
		}
		return sb.toString();
	}

    public static String cleanHex(String input) {
        if (input == null) return "";
        return input.replaceAll("[^a-zA-Z0-9]", "");
    }

    public static byte[] hexStringToBytes(String hex) {
        hex = hex.trim();

        // Must be even length
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException("Invalid hex string length");
        }

        int length = hex.length() / 2;
        byte[] result = new byte[length];

        for (int i = 0; i < length; i++) {
            String byteStr = hex.substring(i * 2, i * 2 + 2);
            result[i] = (byte) Integer.parseInt(byteStr, 16);
        }

        return result;
    }

}


