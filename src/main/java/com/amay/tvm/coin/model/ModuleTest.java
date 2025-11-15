package com.amay.tvm.coin.model;

import java.util.HashMap;
import java.util.Map;

public class ModuleTest extends ModuleResponse {
	public ModuleTest(byte command, byte sequenceNumber, byte[] data) {
		super(command, sequenceNumber, data);
	}
//	Map<Byte,String> map=new HashMap();
//	{
//		map.put((byte) 0x00,"Success");
//		map.put((byte) 0x01,"Fail");
//		map.put((byte) 0x02,"Busy");
//		map.put((byte) 0x03,"Error");
//	}

	public boolean isAck() {
		byte[] d = getData();
		return d.length > 0 && d[0] == 0x00; // assume 0x00 means OK
	}
}


