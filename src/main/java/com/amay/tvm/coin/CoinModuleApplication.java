package com.amay.tvm.coin;


import com.amay.tvm.coin.enums.ModuleTestCode;
import com.amay.tvm.coin.enums.Range;
import com.amay.tvm.coin.model.*;
import com.amay.tvm.coin.service.CoinModuleService;
import com.amay.tvm.coin.service.HoppersRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Scanner;

@Slf4j
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
				System.out.println(
						"\n1) Poll Status" +
								"  2) Get Version" +
								"  3) Dispense" +
								"  4) Dump" +
								"  5) Buzzer On" +
								"  6) Buzzer Off" +
								"  7) Light On" +
								"  8) Light Off" +
								"  9) Multitest" +
								"  10) DE-Jamming Test" +
								"  11) Shutter Test" +
								"  12) Diverter Test" +
								"  13) Acceptance Poll Status" +
								"  14) DE-Jamming With Range" +
								"  15) Get Collection Box ID" +
								"  16) Set Collection Box ID" +
								"  20) Set Collection Box ID"

				);

				//020C050B010000000000003F05003903
				//020C310A00000000000000900000A703

				System.out.print("Select: ");
				String choice = sc.nextLine().trim();
				try {
                    switch (choice) {
                        case "1" -> {
                            ModuleResponse r = service.pollStatus();
                            System.out.println("Response: CMD=" + (r.getCommand() & 0xFF) + " SN=" + (r.getSequenceNumber() & 0xFF) + " DATA=" + r.getData().length + " bytes");
                        }
                        case "2" -> {
                            ModuleResponse r = service.getVersion();
                            System.out.println("Version DATA=" + r.getData().length + " bytes");
                        }
                        case "3" -> {
                            System.out.print("Hopper (1-3): ");
                            byte hopper = Byte.parseByte(sc.nextLine().trim());
                            System.out.print("Quantity (1-49): ");
                            byte qty = Byte.parseByte(sc.nextLine().trim());
                            ModuleResponse r = service.dispenseCoin(hopper, qty);
                            System.out.println("Dispense done, DATA=" + r.getData().length + " bytes");
                        }
                        case "4" -> {
                            System.out.print("Hopper (1-3): ");
                            byte hopper = Byte.parseByte(sc.nextLine().trim());
                            ModuleResponse r = service.dumpHopper(hopper);
                            System.out.println("Dump done, DATA=" + r.getData().length + " bytes");
                        }
                        case "5" -> {
                            ModuleResponse r = service.turnOnBuzzer();
                            System.out.println("Buzzer On: DATA=" + r.getData().length + " bytes");
                        }
                        case "6" -> {
                            ModuleResponse r = service.turnOffBuzzer();
                            System.out.println("Buzzer Off: DATA=" + r.getData().length + " bytes");
                        }
                        case "7" -> {
                            ModuleResponse r = service.turnOnTrayLight();
                            System.out.println("Light On: DATA=" + r.getData().length + " bytes");
                        }
                        case "8" -> {
                            ModuleResponse r = service.turnOffTrayLight();
                            System.out.println("Light Off: DATA=" + r.getData().length + " bytes");
                        }
                        case "9" -> {
//						service.dispenseCoin((byte) 3, (byte)2);
                            System.out.println("Sending multitest dispense commands to all hoppers...");
                            new Thread(() -> {
                                ModuleResponse r = service.dispenseCoin((byte) 3, (byte) 1);
                                System.out.println("FIRST>>>>>>> Dispense done, DATA=" + r.getData().length + " bytes");
                            }).start();
//						Thread.sleep(8000);
                            System.out.println("Sending Second Command...");
                            new Thread(() -> {
                                ModuleResponse r = service.dispenseCoin((byte) 2, (byte) 1);
                                System.out.println("Sending Third Command...");
                                System.out.println("SECOND>>>>>>> Dispense done, DATA=" + r.getData().length + " bytes");
                            }).start();
//						Thread.sleep(8000);
//						new Thread(()-> {
//							ModuleResponse r=service.dispenseCoin((byte) 1, (byte)1);
//						System.out.println("Third>>>>>>> Dispense done, DATA=" + r.getData().length + " bytes");
//						}).start();
                        }
                        case "10" -> {
                            ModuleTest r = (ModuleTest)service.testModule(ModuleTestCode.DE_JAMMING);
                            System.out.println("DE-JAMMING Test: DATA=" + r.getData().length + " bytes"+" "+r.isAck());
                        }
						case "11" -> {
							ModuleResponse r = service.testModule(ModuleTestCode.COIN_SHUTTER);
							System.out.println("SHUTTER Test: DATA=" + r.getData().length + " bytes");
						}
						case "12" -> {
							ModuleResponse r = service.testModule(ModuleTestCode.DIVERTER);
							System.out.println("DIVERTER Test: DATA=" + r.getData().length + " bytes");
						}
						case "13" -> {
							AcceptancePollingStatusResponse r = (AcceptancePollingStatusResponse)service.acceptancePollStatus(new CoinPollRequest(
									false,   // shutterOpen (Bit7)
									false,  // clearRegister (Bit6)
									true,   // inhibit Rs20 (Bit4)
									true,  // inhibit Rs10 (Bit3)
									true,  // inhibit Rs5  (Bit2)
									true,   // inhibit Rs2  (Bit1)
									true    // inhibit Rs1  (Bit0)
							).getStatusByte());
							System.out.println("Polling: DATA=" + r.getData().length + " bytes"+ " "+r.parse().toString() );
						}
						case "14" -> {
							System.out.print("Enter Range Number 1-3 : ");
							byte id = Byte.parseByte(sc.nextLine().trim());
							DeJammingResponse r = (DeJammingResponse) service.getDeJamming(Arrays.stream(Range.values()).filter(x->x.getCode()==id).findAny().orElse(Range.ONE));
							System.out.println("DIVERTER Test: DATA=" + r.getData().length + " bytes"+" "+r.getStatus());
						}
						case "15" -> {
							CollectionBoxIdResponse r = (CollectionBoxIdResponse) service.getCollectionBoxId();
							System.out.println("GET COLLECTION BOX ID Test: DATA=" + r.getData().length + " bytes");
						}
						case "16" -> {
							System.out.print("Enter Collection Box Id : ");
							byte id = Byte.parseByte(sc.nextLine().trim());
							ModuleResponse r = service.setCollectionBoxId(id);
							System.out.println("Set COLLECTION BOX ID Test: DATA=" + r.getData().length + " bytes");
						}
                        default -> {
                            break;
                        }
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


