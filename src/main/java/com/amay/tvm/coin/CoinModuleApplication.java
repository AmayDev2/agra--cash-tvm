package com.amay.tvm.coin;


import com.amay.tvm.coin.model.ModuleResponse;
import com.amay.tvm.coin.service.CoinModuleService;
import com.amay.tvm.coin.service.HoppersRegistry;

import java.util.Scanner;

public class CoinModuleApplication {
	public static void main(String[] args) {
		CoinModuleService service = new CoinModuleService();
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter COM port (e.g., COM3): ");
		String port = sc.nextLine().trim();
		try {
			service.connect(port);
			System.out.println("Connected to " + port);
			while (true) {
				System.out.println("\n1) Poll Status  2) Get Version  3) Dispense  4) Dump  5) Buzzer On  6) Buzzer Off  7) Light On  8) Light Off  9)Multitest   10) Exit");
				System.out.print("Select: ");
				String choice = sc.nextLine().trim();
				try {
					if ("1".equals(choice)) {
						ModuleResponse r = service.pollStatus();
						System.out.println("Response: CMD=" + (r.getCommand() & 0xFF) + " SN=" + (r.getSequenceNumber() & 0xFF) + " DATA=" + r.getData().length + " bytes");
					} else if ("2".equals(choice)) {
						ModuleResponse r = service.getVersion();
						System.out.println("Version DATA=" + r.getData().length + " bytes");
					} else if ("3".equals(choice)) {
						System.out.print("Hopper (1-3): ");
						byte hopper = Byte.parseByte(sc.nextLine().trim());
						System.out.print("Quantity (1-49): ");
						byte qty = Byte.parseByte(sc.nextLine().trim());
						ModuleResponse r = service.dispenseCoin(hopper, qty);
						System.out.println("Dispense done, DATA=" + r.getData().length + " bytes");
					} else if ("4".equals(choice)) {
						System.out.print("Hopper (1-3): ");
						byte hopper = Byte.parseByte(sc.nextLine().trim());
						ModuleResponse r = service.dumpHopper(hopper);
						System.out.println("Dump done, DATA=" + r.getData().length + " bytes");
					} else if ("5".equals(choice)) {
						ModuleResponse r = service.turnOnBuzzer();
						System.out.println("Buzzer On: DATA=" + r.getData().length + " bytes");
					} else if ("6".equals(choice)) {
						ModuleResponse r = service.turnOffBuzzer();
						System.out.println("Buzzer Off: DATA=" + r.getData().length + " bytes");
					} else if ("7".equals(choice)) {
						ModuleResponse r = service.turnOnTrayLight();
						System.out.println("Light On: DATA=" + r.getData().length + " bytes");
					} else if ("8".equals(choice)) {
						ModuleResponse r = service.turnOffTrayLight();
						System.out.println("Light Off: DATA=" + r.getData().length + " bytes");
					} else if("9".equals(choice)) {
//						service.dispenseCoin((byte) 3, (byte)2);
						System.out.println("Sending multitest dispense commands to all hoppers...");
						ModuleResponse r =service.dispenseCoin((byte) 3, (byte)1);
						System.out.println("FIRST>>>>>>> Dispense done, DATA=" + r.getData().length + " bytes");
						Thread.sleep(8000);
						System.out.println("Sending Second Command...");
						 r =service.dispenseCoin((byte) 2, (byte)1);
						System.out.println("Sending Third Command...");
						System.out.println("SECOND>>>>>>> Dispense done, DATA=" + r.getData().length + " bytes");
						Thread.sleep(8000);
						r=service.dispenseCoin((byte) 1, (byte)1);
						System.out.println("Third>>>>>>> Dispense done, DATA=" + r.getData().length + " bytes");
					}else if ("10".equals(choice)) {
						break;
					}
				} catch (Exception ex) {
					System.out.println("Error: " + ex.getMessage());
				}
			}
		} finally {
			service.disconnect();
			System.out.println("Disconnected.");
		}
	}
}


