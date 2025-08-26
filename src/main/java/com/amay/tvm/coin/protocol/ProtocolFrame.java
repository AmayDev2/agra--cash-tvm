package com.amay.tvm.coin.protocol;



import com.amay.tvm.coin.constants.ProtocolConstants;

import java.util.Arrays;

public class ProtocolFrame {
	private final byte command;
	private final byte sequenceNumber;
	private final byte[] data;

	public ProtocolFrame(byte command, byte sequenceNumber, byte[] data) {
		this.command = command;
		this.sequenceNumber = sequenceNumber;
		this.data = data == null ? new byte[0] : Arrays.copyOf(data, data.length);
	}

	public byte getCommand() { return command; }
	public byte getSequenceNumber() { return sequenceNumber; }
	public byte[] getData() { return Arrays.copyOf(data, data.length); }

	public byte[] toByteArray() {
		// Calculate length: CMD + SN + DATA (excludes STX, LEN, BCC, ETX)
		byte length = (byte) (2 + data.length); // CMD(1) + SN(1) + DATA(n)

		// Calculate BCC: XOR of Length + Command + SN + Data
		byte bcc = length;
		bcc ^= command;
		bcc ^= sequenceNumber;
		for (byte b : data) {
			bcc ^= b;
		}

		// Build complete frame: STX + LEN + CMD + SN + DATA + BCC + ETX
		int frameSize = 1 + 1 + 1 + 1 + data.length + 1 + 1; // STX+LEN+CMD+SN+DATA+BCC+ETX
		byte[] frame = new byte[frameSize];

		int idx = 0;
		frame[idx++] = ProtocolConstants.STX;      // 0x02
		frame[idx++] = length;                     // Length of CMD+SN+DATA
		frame[idx++] = command;                    // Command byte
		frame[idx++] = sequenceNumber;             // Sequence number

		// Copy data if any
		if (data.length > 0) {
			System.arraycopy(data, 0, frame, idx, data.length);
			idx += data.length;
		}

		frame[idx++] = bcc;                        // BCC checksum
		frame[idx] = ProtocolConstants.ETX;        // 0x03


		return frame;
	}

	public static ProtocolFrame parse(byte[] frameData) {
		if (frameData == null || frameData.length < 6) {
			throw new ProtocolException("Frame too short (minimum 6 bytes required)");
		}

		// Validate STX
		if (frameData[0] != ProtocolConstants.STX) {
			throw new ProtocolException("Invalid STX: expected 0x02, got 0x" +
					String.format("%02X", frameData[0]));
		}

		// Validate ETX
		if (frameData[frameData.length - 1] != ProtocolConstants.ETX) {
			throw new ProtocolException("Invalid ETX: expected 0x03, got 0x" +
					String.format("%02X", frameData[frameData.length - 1]));
		}

		// Extract length
		byte length = frameData[1];

		// Determine indexes using LEN, but tolerate extra stuffed bytes before ETX
		int payloadEndExclusive = 2 + (length & 0xFF); // index after CMD+SN+DATA
		int bccIndex = payloadEndExclusive;           // BCC follows CMD+SN+DATA
		int etxIndex = frameData.length - 1;          // ETX at the end
		if (bccIndex >= etxIndex) {
			throw new ProtocolException("Frame too short for BCC/ETX given LEN=" + (length & 0xFF));
		}

		// Extract components
		byte command = frameData[2];
		byte sequenceNumber = frameData[3];

		// Extract data (if any)
		int dataLength = (length & 0xFF) - 2; // length includes CMD + SN + DATA
		if (dataLength < 0) {
			throw new ProtocolException("Invalid LEN (" + (length & 0xFF) + ") for CMD/SN");
		}
		byte[] data = new byte[dataLength];
		if (dataLength > 0) {
			System.arraycopy(frameData, 4, data, 0, dataLength);
		}

		// Extract and validate BCC located using LEN
		byte receivedBcc = frameData[bccIndex];
		byte expectedBcc = length;
		expectedBcc ^= command;
		expectedBcc ^= sequenceNumber;
		for (byte b : data) {
			expectedBcc ^= b;
		}
		if (receivedBcc != expectedBcc) {
			throw new ProtocolException("BCC mismatch: expected 0x" +
					String.format("%02X", expectedBcc) + ", got 0x" +
					String.format("%02X", receivedBcc));
		}

		return new ProtocolFrame(command, sequenceNumber, data);
	}

	@Override
	public String toString() {
		return String.format("ProtocolFrame{cmd=0x%02X, sn=%d, dataLen=%d}",
				command & 0xFF, sequenceNumber & 0xFF, data.length);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;

		ProtocolFrame that = (ProtocolFrame) obj;
		return command == that.command &&
				sequenceNumber == that.sequenceNumber &&
				Arrays.equals(data, that.data);
	}

	@Override
	public int hashCode() {
		int result = Byte.hashCode(command);
		result = 31 * result + Byte.hashCode(sequenceNumber);
		result = 31 * result + Arrays.hashCode(data);
		return result;
	}
}
