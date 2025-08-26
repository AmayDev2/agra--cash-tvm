package com.amay.tvm.coin.protocol;


import com.amay.tvm.coin.constants.ProtocolConstants;

import java.util.ArrayList;
import java.util.List;

public final class DataEscapeUtil {
	private DataEscapeUtil() {}

	public static byte[] escapeFrame(byte[] frame) {
		if (frame == null || frame.length == 0) return new byte[0];
		int stxIndex = -1;
		int etxIndex = -1;
		for (int i = 0; i < frame.length; i++) {
			if (frame[i] == ProtocolConstants.STX) { stxIndex = i; break; }
		}
		for (int i = frame.length - 1; i >= 0; i--) {
			if (frame[i] == ProtocolConstants.ETX) { etxIndex = i; break; }
		}
		if (stxIndex < 0 || etxIndex < 0 || etxIndex <= stxIndex) {
			// Not a framed message; return as-is
			return frame.clone();
		}
		List<Byte> out = new ArrayList<>(frame.length + 8);
		// copy bytes up to and including STX
		for (int i = 0; i <= stxIndex; i++) out.add(frame[i]);
		// copy LEN byte unchanged
		out.add(frame[stxIndex + 1]);
		// escape only the body between STX and ETX, EXCLUDING the LEN byte
		for (int i = stxIndex + 2; i < etxIndex; i++) {
			byte b = frame[i];
			// Device protocol: escape by prefixing DLE, keep byte unchanged (no XOR)
			if (b == ProtocolConstants.STX || b == ProtocolConstants.ETX || b == ProtocolConstants.DLE) {
				out.add(ProtocolConstants.DLE);
				out.add(b);
			} else {
				out.add(b);
			}
		}
		// copy ETX and any trailing bytes after ETX (should be none)
		for (int i = etxIndex; i < frame.length; i++) out.add(frame[i]);
		byte[] result = new byte[out.size()];
		for (int i = 0; i < out.size(); i++) result[i] = out.get(i);
		return result;
	}

	public static byte[] unescapeFrame(byte[] escaped) {
		if (escaped == null || escaped.length == 0) return new byte[0];
		int stxIndex = -1;
		int etxIndex = -1;
		for (int i = 0; i < escaped.length; i++) {
			if (escaped[i] == ProtocolConstants.STX) { stxIndex = i; break; }
		}
		for (int i = escaped.length - 1; i >= 0; i--) {
			if (escaped[i] == ProtocolConstants.ETX) { etxIndex = i; break; }
		}
		if (stxIndex < 0 || etxIndex < 0 || etxIndex <= stxIndex) {
			return escaped.clone();
		}
		List<Byte> out = new ArrayList<>(escaped.length);
		// copy up to and including STX
		for (int i = 0; i <= stxIndex; i++) out.add(escaped[i]);
		// copy LEN byte unchanged
		out.add(escaped[stxIndex + 1]);
		// unescape body only, EXCLUDING the LEN byte
		for (int i = stxIndex + 2; i < etxIndex; i++) {
			byte b = escaped[i];
			if (b == ProtocolConstants.DLE) {
				if (i + 1 >= etxIndex) {
					throw new ProtocolException("Dangling DLE before ETX");
				}
				byte next = escaped[i + 1];
				// Only treat DLE followed by control as escaped; otherwise, keep DLE as data
				if (next == ProtocolConstants.STX || next == ProtocolConstants.ETX || next == ProtocolConstants.DLE) {
					out.add(next);
					i++; // consume next
				} else {
					out.add(b);
				}
			} else {
				out.add(b);
			}
		}
		// copy ETX and any trailing bytes after ETX (should be none)
		for (int i = etxIndex; i < escaped.length; i++) out.add(escaped[i]);
		byte[] result = new byte[out.size()];
		for (int i = 0; i < out.size(); i++) result[i] = out.get(i);
		return result;
	}

    public static boolean isEscapeEnabled(ProtocolFrame frame) {
		if (frame == null) return false;
		byte[] data = frame.getData();
		if (data == null || data.length == 0) return false;
		for (byte b : data) {
			if (b == ProtocolConstants.STX || b == ProtocolConstants.ETX || b == ProtocolConstants.DLE) {
				return true;
			}
		}
		return false;
    }
}


