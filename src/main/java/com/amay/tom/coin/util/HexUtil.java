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
}


