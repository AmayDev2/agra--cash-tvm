package com.amay.tvm.coin.constants;

import java.util.HashMap;
import java.util.Map;

public final class ErrorCodes {
	private static final Map<Integer, String> CODE_TO_MESSAGE = new HashMap<>();
	static {
		CODE_TO_MESSAGE.put(60, "Hopper 1 not in position");
		CODE_TO_MESSAGE.put(61, "Hopper 1 empty");
		CODE_TO_MESSAGE.put(62, "Hopper 1 failure");

		CODE_TO_MESSAGE.put(70, "Hopper 2 not in position");
		CODE_TO_MESSAGE.put(71, "Hopper 2 empty");
		CODE_TO_MESSAGE.put(72, "Hopper 2 failure");

		CODE_TO_MESSAGE.put(80, "Hopper 3 not in position");
		CODE_TO_MESSAGE.put(81, "Hopper 3 empty");
		CODE_TO_MESSAGE.put(82, "Hopper 3 failure");
	}

	private ErrorCodes() {}

	public static String describe(int code) {
		return CODE_TO_MESSAGE.getOrDefault(code, "Unknown error code: " + code);
	}
}


