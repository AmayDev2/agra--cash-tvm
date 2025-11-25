package com.amay.tom.coin;

import com.amay.tom.coin.enums.Diverter;
import com.amay.tom.coin.enums.Escrow;
import com.amay.tom.coin.enums.ModuleTestCode;
import com.amay.tom.coin.enums.Range;
import com.amay.tom.coin.model.*;
import com.amay.tom.coin.service.CoinModuleService;
import com.amay.tom.config.LoggerTag;
import javafx.scene.control.SingleSelectionModel;
import org.tinylog.Logger;

import java.util.Arrays;


public enum CoinModuleInterface {
	INSTANCE;
	CoinModuleService service;
	private boolean isPoolingAllowed = true;
	private String comPort;

	public ModuleTest testModule(ModuleTestCode selectionModel) {
		return (ModuleTest) service.testModule(selectionModel);

	}

	public CoinModuleService setupCoinModule(String comPort) {
		service = new CoinModuleService();
//		HoppersRegistry.INSTANCE.setHoppers(5,10,10,     0,0,0);
		this.comPort = comPort;
		connect(comPort);
		return service;
	}

	public String closeCoinModule() {
		service.disconnect();
		return "Closed";
	}

	private void connect(String comPort) {
		try {
			service.connect(comPort);
		} catch (Exception e) {
			Logger.tag(LoggerTag.APP).error("{}", e.fillInStackTrace());
		}
	}

	public void reconnect() {
		connect(comPort);
	}

	public CollectionBoxIdResponse getBoxId() {
		return (CollectionBoxIdResponse) service.getCollectionBoxId();
	}

	public CollectionBoxIdSetResponse setBoxId(byte id) {
		return (CollectionBoxIdSetResponse) service.setCollectionBoxId(id);
	}

	public DeJammingResponse deJamming(Range range) {
		return (DeJammingResponse) service.getDeJamming(range);

	}

	public ModuleResponse coinChange(int hopper, int quantity){
        return service.dispenseCoin((byte) hopper, (byte) quantity);
	}

//	      case"10"->
//
//	{
//		ModuleTest r = (ModuleTest) service.testModule(ModuleTestCode.DE_JAMMING);
//		System.out.println("DE-JAMMING Test: DATA=" + r.getData().length + " bytes" + " " + r.isAck());
//	}
//						case"11"->
//
//	{
//		ModuleResponse r = service.testModule(ModuleTestCode.COIN_SHUTTER);
//		System.out.println("SHUTTER Test: DATA=" + r.getData().length + " bytes");
//	}
//						case"12"->
//
//	{
//		ModuleResponse r = service.testModule(ModuleTestCode.DIVERTER);
//		System.out.println("DIVERTER Test: DATA=" + r.getData().length + " bytes");
//	}
//						case"13"->
//
//	{
//		AcceptancePollingStatusResponse r = (AcceptancePollingStatusResponse) service.acceptancePollStatus(new CoinPollRequest(
//				false,   // shutterOpen (Bit7)
//				false,  // clearRegister (Bit6)
//				true,   // inhibit Rs20 (Bit4)
//				true,  // inhibit Rs10 (Bit3)
//				true,  // inhibit Rs5  (Bit2)
//				true,   // inhibit Rs2  (Bit1)
//				true    // inhibit Rs1  (Bit0)
//		).getStatusByte());
//		System.out.println("Polling: DATA=" + r.getData().length + " bytes" + " " + r.parse().toString());
//	}
//						case"14"->
//
//	{
//		System.out.print("Enter Range Number 1-3 : ");
//
//	}
//						case"15"->
//
//	{
//		CollectionBoxIdResponse r = (CollectionBoxIdResponse) service.getCollectionBoxId();
//		System.out.println("GET COLLECTION BOX ID Test: DATA=" + r.getData().length + " bytes");
//	}
//						case"16"->
//
//	{
//		System.out.print("Enter Collection Box Id : ");
//		byte id = Byte.parseByte(sc.nextLine().trim());
//		ModuleResponse r = service.setCollectionBoxId(id);
//		System.out.println("Set COLLECTION BOX ID Test: DATA=" + r.getData().length + " bytes");
//	}

	//	public boolean isDenominationPossible(List<AmountDetail> list, int amount){
//		// Original test case - modified for standard denominations
//		HaveAmountObject haveAmount = new HaveAmountObject(list);
//		ReturnableAmountObject result = new MaxChangePossibleService().getReturnableAmount(new ReturnableAmountObject(new ArrayList<>()),haveAmount, amount, 0);
//		return result.totalAmount == amount;
//	}
//
//	public boolean isDenominationPossibleAll(List<AmountDetail> list,int amount){
//		HaveAmountObject haveAmountCoin = new HaveAmountObject(HoppersRegistry.INSTANCE.getHoppers());
//
//		// Original test case - modified for standard denominations
//		HaveAmountObject haveAmount = new HaveAmountObject(list);
//
//		HaveAmountObject combinedHaveAmount = new HaveAmountObject(new ArrayList<>());
//		combinedHaveAmount.amountDetailList.addAll(haveAmount.amountDetailList);
//		combinedHaveAmount.amountDetailList.addAll(haveAmountCoin.amountDetailList);
//
//		combinedHaveAmount.amountDetailList.sort((o1, o2) -> Integer.compare(o2.getAmount(), o1.getAmount())); // Sort in descending order of amount
//
//		ReturnableAmountObject result = new MaxChangePossibleService().getReturnableAmount(new ReturnableAmountObject(new ArrayList<>()),combinedHaveAmount, amount, 0);
//		result.amountDetailList.forEach(ad -> Logger.info("Using Denomination: {} x {} for {}", ad.getAmount(), ad.getQuantity(),amount));
//		return result.totalAmount == amount;
//	}
//
//	public CoinResponseDecoder.CoinModuleDispenseResponse dispense(int amount){
//		isPoolingAllowed=false;
//		List<CoinResponseDecoder.DispenseResult> list=new ArrayList<>();
//		CoinResponseDecoder.CoinModuleDispenseResponse dispenseResponse=new CoinResponseDecoder.CoinModuleDispenseResponse(false,0,"msg",list);;
//		try{
//		HaveAmountObject haveAmount = new HaveAmountObject(HoppersRegistry.INSTANCE.getHoppers());
//		MaxChangePossibleService maxChangePossibleService=	new MaxChangePossibleService();
//		ReturnableAmountObject result = maxChangePossibleService.getReturnableAmount(new ReturnableAmountObject(new ArrayList<>()),haveAmount, amount, 0);
//		if(result.getAmountDetailList().size()>1){
//			maxChangePossibleService.optimizeForSingleHoper(result,haveAmount);
//			Logger.tag(LoggerTag.BUSS).info("Update to one hopper");
//		}
//
//		Logger.tag(LoggerTag.BUSS).info("Dispensing coins for amount: "+amount+", possible amount: "+result.totalAmount);
//		result.amountDetailList.forEach(ad -> Logger.info("Dispensing Denomination: {} x {} for", ad.getAmount(), ad.getQuantity(),amount));
//		if(result.totalAmount!=amount && !service.isConnected()){
//			return dispenseResponse;
//		}
//			service.end();
//
//		int delayTime=0;
//		for(AmountDetail amountDetail:result.amountDetailList) {
//			Thread.sleep(delayTime); // wait before sending next command
//			delayTime=8000;
//			int hopper= Integer.parseInt(amountDetail.getContainerId());
//			Logger.tag(LoggerTag.APP).info("Sending >>>>> Command to Hopper: {} for Quantity: {}", hopper, amountDetail.getQuantity());
//			ModuleResponse response= service.dispenseCoin((byte) hopper, (byte) amountDetail.getQuantity());
//			Logger.tag(LoggerTag.BUSS).info("Dispense done, DATA=" + response.getData().length + " bytes");
//			CoinResponseDecoder.DispenseResult dispenseResult=CoinResponseDecoder.decodeDispenseResponse(response.getData());
//			HoppersRegistry.INSTANCE.updateHopperDeduct(hopper,dispenseResult.quantityDispensed);
//			list.add(dispenseResult);
//		}
//
//
//
//		boolean statue = false;  // NO NEED
//		for(CoinResponseDecoder.DispenseResult dispenseResult:list){
//			statue=statue || dispenseResult.success;
//		}
//
//		dispenseResponse.message=statue?"Dispense completed":"Dispense Failed";
//		dispenseResponse.success=statue;
//		dispenseResponse.setTotalAmount();
//
//		Logger.tag(LoggerTag.BUSS).info("Dispense response: "+dispenseResponse.amountDispensed+" "+dispenseResponse.success);
//		}catch (Exception e){
//			Logger.tag(LoggerTag.APP).error("ERROR DURING DISPENSE COINS : ",e.getMessage());
//		}finally {
//			isPoolingAllowed=true;
//		}
//		return dispenseResponse;
//	}
//
//	public ReturnableAmountObject getMaxChangeableAmount(HaveAmountObject haveAmountObject, long amount){
//		ReturnableAmountObject result = new MaxChangePossibleService().getReturnableAmount(new ReturnableAmountObject(new ArrayList<>()),haveAmountObject, (int) amount, 0);
//		Logger.tag(LoggerTag.BUSS).info("Max changeable amount for {} is {}",amount,result.totalAmount);
//		return result;
//
//	}
//
	public CoinDumpResponse dumpHopper(int hopperId) {
		isPoolingAllowed = true;
		Logger.tag(LoggerTag.APP).info("Requesting Dump " + hopperId);
		try {
			return (CoinDumpResponse) service.dumpHopper((byte) hopperId);
		} catch (Exception e) {
			Logger.tag(LoggerTag.APP).error(e.getMessage());
		} finally {
			isPoolingAllowed = true;
		}
		return null;

    }

		private PollingStatusResponse pollingStatusResponse;
		public PollingStatusResponse pooling () {
			if (!isPoolingAllowed) return pollingStatusResponse;
			pollingStatusResponse = (PollingStatusResponse) service.pollStatus();
			Logger.tag(LoggerTag.APP).info(pollingStatusResponse.toString());
			return pollingStatusResponse;

		}


	public void turnOffBuzzer() {
		service.turnOffBuzzer();
		Logger.tag(LoggerTag.APP).warn("TURN OFF BUZZER");
	}

	public void turnOnBuzzer() {
		service.turnOnBuzzer();
		Logger.tag(LoggerTag.APP).warn("TURN ON BUZZER");
	}

	public void buzzerStatus() {
		service.buzzerStatus();
	}


	public AcceptancePollingStatusResponse poolingAcceptance(CoinPollRequest req) {
		return  (AcceptancePollingStatusResponse)service.acceptancePollStatus(req.getStatusByte());
	}

	public ModuleResponse getVersion() throws Exception  {
			return service.getVersion();
	}

	public ModuleResponse getModuleReset() throws Exception  {
		return service.getModuleReset();
	}

	public ModuleResponse controlDivert(Diverter isReturnTrayOrCollectionBox) throws Exception  {
		return service.controlDivertCommand(isReturnTrayOrCollectionBox==Diverter.COLLECTION_BOX);
	}

	public ModuleResponse controlEscrow(Escrow isCoinReturnOrCollection) throws Exception  {
		return service.controlEscrowCommand(isCoinReturnOrCollection==Escrow.COIN_COLLECTION);
	}
}


