package com.amay.tvm.coin.protocol;


import com.amay.tvm.backend.enums.LoggerTag;
import com.amay.tvm.coin.constants.ProtocolConstants;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.Collections;
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
		List<Byte> out = getBytes(frame, stxIndex, etxIndex);
		byte[] result = new byte[out.size()];
		for (int i = 0; i < out.size(); i++) result[i] = out.get(i);
		return result;
	}

	private static List<Byte> getBytes(byte[] frame, int stxIndex, int etxIndex) {
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
		return out;
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

    public static boolean isEscapeEnabled(ProtocolFrame frame) { //0205000101AB04AA03 dis
		if (frame == null) return false;
		byte[] dataForEscape = frame.getData();
		if(frame.getCommand() == ProtocolConstants.STX || frame.getCommand() == ProtocolConstants.ETX || frame.getCommand()  == ProtocolConstants.DLE
		|| frame.getSequenceNumber() == ProtocolConstants.STX || frame.getSequenceNumber() == ProtocolConstants.ETX || frame.getSequenceNumber()  == ProtocolConstants.DLE)return true;
		if (dataForEscape == null) return false;
		for (byte b : dataForEscape) {
			Logger.tag(LoggerTag.APP).info(" For Escape Data byte: 0x{}", String.format("%02X", b));
			if (b == ProtocolConstants.STX || b == ProtocolConstants.ETX || b == ProtocolConstants.DLE) {
				return true;
			}
		}
		return false;
    }
}



/*
* Control code definitions of the communication protocol specification are as following:

Code Value (Hex) Description
STX 02h Start of Text
ETX 03h End of Text
DLE 10h Data Link Escape


3.2. Frame format specification
Code
STX

(1)
Length

(2)
Command

(3)
SN

(4)
DATA

(5)
BCC

(6)
ETX

(7)
Bytes 1 1 1 1 Variable 1 1
Type HEX HEX HEX Hex HEX HEX HEX


STX
Communication frame start character 0x02.

Length
The length of data is including the Command, SN and DATA, but does not contain
DLE.

Command
The command code is communication agreement of both sides.

SN
This is the unique serial number of the command. Range of the SN is from 1 to 255.
Each command uses one serial number. For the next command, the number will plus one
(roll back to 1 when over 255). The host will send the command with same of serial
number again if timeout. The firmware will only response but without action when
BCC Processing Area
Length Area
DLE Area
receiving the same command and the same SN.
DATA
Data is the parameter or the status of Command.
BCC
Check for communication frames of data integrity. XOR calculation method is used
from the Length to DATA. (BCC code is calculated for the original data before DLE.)
ETX
End character for Communication frame 0x03
DLE Processing Area
DLE is used to escape the special character within the message. For example, 0x02,
0x03 exists in a message. In this case, DLE must be inserted. The DLE character is not
calculated for the length of the message or the BCC.
Page 6 of 25
Coin Module Protocol Specification
3.3. Data Escape
In communications, in addition to STX 0x02 and ETX 0x03, data 0x02, 0x03 and 0x10
should be transformed according to below method.
Direction: PC - > Control module
0x02 converted to 0x10 0x02
0x03 converted to 0x10 0x03
0x10 converted to 0x10 0x10
Direction: Control module ->PC
0x10 0x02 converted 0x02
0x10 0x03 converted 0x03
0x10 0x10 converted 0x10
Example:
A list of data will be sent including STX 0x02 and ETX 0x03
0x02 0x04 0x30 0x01 0x03 0x10 0x26 0x03
After data escapes, new data as follows:
0x02 0x04 0x30 0x01 0x10 0x03 0x10 0x10 0x26 0x03
Received data after conversion generates reduction data as follows:
0x02 0x04 0x30 0x01 0x03 0x10 0x26 0x03
*
* */


