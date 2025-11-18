package com.amay.tom.coin.protocol;

import java.util.concurrent.atomic.AtomicInteger;

public class SequenceNumberManager {
	private final AtomicInteger current = new AtomicInteger(0);

	public byte next() {
		int value = current.updateAndGet(prev -> {
			int nextVal = prev + 1;
			if (nextVal <= 0 || nextVal > 255) {
				return 1;
			}
			return nextVal;
		});
		return (byte) (value & 0xFF);
	}

	public void resetTo(byte sequenceNumber) {
		int v = sequenceNumber & 0xFF;
		if (v < 1 || v > 255) v = 1;
		current.set(v);
	}
}


