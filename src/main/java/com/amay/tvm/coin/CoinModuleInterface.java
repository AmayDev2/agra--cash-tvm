package com.amay.tvm.coin;


import com.amay.tvm.backend.enums.LoggerTag;
import com.amay.tvm.coin.model.AmountDetail;
import com.amay.tvm.coin.model.HaveAmountObject;
import com.amay.tvm.coin.model.ModuleResponse;
import com.amay.tvm.coin.model.ReturnableAmountObject;
import com.amay.tvm.coin.service.CoinModuleService;
import com.amay.tvm.coin.service.CoinResponseDecoder;
import com.amay.tvm.coin.service.HoppersRegistry;
import com.amay.tvm.coin.service.MaxChangePossibleService;
import javafx.application.Platform;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public enum CoinModuleInterface {
	INSTANCE;
	CoinModuleService service;
	public CoinModuleService setupCoinModule(String comPort){
		service = new CoinModuleService();
		service.connect(comPort);
		HoppersRegistry.INSTANCE.setHoppers(5,10,10,     0,0,0);
		return service;
	}
	public void closeCoinModule(){
		service.disconnect();
	}

	public boolean isDenominationPossible(List<AmountDetail> list,int amount){
		// Original test case - modified for standard denominations
		HaveAmountObject haveAmount = new HaveAmountObject(list);
		ReturnableAmountObject result = new MaxChangePossibleService().getReturnableAmount(new ReturnableAmountObject(new ArrayList<>()),haveAmount, amount, 0);
		return result.totalAmount == amount;
	}

	public boolean isDenominationPossibleAll(List<AmountDetail> list,int amount){
		HaveAmountObject haveAmountCoin = new HaveAmountObject(HoppersRegistry.INSTANCE.getHoppers());

		// Original test case - modified for standard denominations
		HaveAmountObject haveAmount = new HaveAmountObject(list);

		HaveAmountObject combinedHaveAmount = new HaveAmountObject(new ArrayList<>());
		combinedHaveAmount.amountDetailList.addAll(haveAmount.amountDetailList);
		combinedHaveAmount.amountDetailList.addAll(haveAmountCoin.amountDetailList);

		combinedHaveAmount.amountDetailList.sort((o1, o2) -> Integer.compare(o2.getAmount(), o1.getAmount())); // Sort in descending order of amount

		ReturnableAmountObject result = new MaxChangePossibleService().getReturnableAmount(new ReturnableAmountObject(new ArrayList<>()),combinedHaveAmount, amount, 0);
		result.amountDetailList.forEach(ad -> Logger.info("Using Denomination: {} x {} for {}", ad.getAmount(), ad.getQuantity(),amount));
		return result.totalAmount == amount;
	}

	public CoinResponseDecoder.CoinModuleDispenseResponse dispense(int amount){
		List<CoinResponseDecoder.DispenseResult> list=new ArrayList<>();
		CoinResponseDecoder.CoinModuleDispenseResponse dispenseResponse=new CoinResponseDecoder.CoinModuleDispenseResponse(false,0,"msg",list);;
		try{
		HaveAmountObject haveAmount = new HaveAmountObject(HoppersRegistry.INSTANCE.getHoppers());
		MaxChangePossibleService maxChangePossibleService=	new MaxChangePossibleService();
		ReturnableAmountObject result = maxChangePossibleService.getReturnableAmount(new ReturnableAmountObject(new ArrayList<>()),haveAmount, amount, 0);
		if(result.getAmountDetailList().size()>1){
			maxChangePossibleService.optimizeForSingleHoper(result,haveAmount);
			Logger.tag(LoggerTag.BUSS).info("Update to one hopper");
		}

		Logger.tag(LoggerTag.BUSS).info("Dispensing coins for amount: "+amount+", possible amount: "+result.totalAmount);
		result.amountDetailList.forEach(ad -> Logger.info("Dispensing Denomination: {} x {} for", ad.getAmount(), ad.getQuantity(),amount));
		if(result.totalAmount!=amount && !service.isConnected()){
			return dispenseResponse;
		}

		int delayTime=0;
		for(AmountDetail amountDetail:result.amountDetailList) {
			Thread.sleep(delayTime); // wait before sending next command
			delayTime=8000;
			int hopper= Integer.parseInt(amountDetail.getContainerId());
			Logger.tag(LoggerTag.APP).info("Sending >>>>> Command to Hopper: {} for Quantity: {}", hopper, amountDetail.getQuantity());
			ModuleResponse response= service.dispenseCoin((byte) hopper, (byte) amountDetail.getQuantity());
			Logger.tag(LoggerTag.BUSS).info("Dispense done, DATA=" + response.getData().length + " bytes");
			CoinResponseDecoder.DispenseResult dispenseResult=CoinResponseDecoder.decodeDispenseResponse(response.getData());
			HoppersRegistry.INSTANCE.updateHopper(hopper,dispenseResult.quantityDispensed);
			list.add(dispenseResult);
		}

/*			ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
			int delay = 0;

			for (AmountDetail amountDetail : result.amountDetailList) {
				int hopper = Integer.parseInt(amountDetail.getContainerId());
				int quantity = amountDetail.quantity;

				scheduler.schedule(() -> {
					try {
						ModuleResponse response = service.dispenseCoin((byte) hopper, (byte) quantity);
						CoinResponseDecoder.DispenseResult dispenseResult =
								CoinResponseDecoder.decodeDispenseResponse(response.getData());
						HoppersRegistry.INSTANCE.updateHopper(hopper, dispenseResult.quantityDispensed);

						list.add(dispenseResult);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}, delay, TimeUnit.MILLISECONDS);

				delay += 8000; // space tasks by 8 seconds
			}*/

		boolean statue = false;  // NO NEED
		for(CoinResponseDecoder.DispenseResult dispenseResult:list){
			statue=statue || dispenseResult.success;
		}

		dispenseResponse.message=statue?"Dispense completed":"Dispense Failed";
		dispenseResponse.success=statue;
		dispenseResponse.setTotalAmount();

		Logger.tag(LoggerTag.BUSS).info("Dispense response: "+dispenseResponse);
		}catch (Exception e){
			Logger.tag(LoggerTag.APP).error("ERROR DURING DISPENSE COINS : ",e.getMessage());
		}
		return dispenseResponse;
	}

	public ReturnableAmountObject getMaxChangeableAmount(HaveAmountObject haveAmountObject, long amount){
		ReturnableAmountObject result = new MaxChangePossibleService().getReturnableAmount(new ReturnableAmountObject(new ArrayList<>()),haveAmountObject, (int) amount, 0);
		Logger.tag(LoggerTag.BUSS).info("Max changeable amount for {} is {}",amount,result.totalAmount);
		return result;

	}

    public void dumpHopper(int hopperId) {
		ModuleResponse response=service.dumpHopper((byte) hopperId);
		Logger.tag(LoggerTag.BUSS).info("Dispense done, DATA=" + response.getData().length + " bytes");
		HoppersRegistry.INSTANCE.resetHopper(hopperId);
    }
}


