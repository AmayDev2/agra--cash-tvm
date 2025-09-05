package com.amay.tvm.coin;


import com.amay.tvm.coin.model.AmountDetail;
import com.amay.tvm.coin.model.HaveAmountObject;
import com.amay.tvm.coin.model.ModuleResponse;
import com.amay.tvm.coin.model.ReturnableAmountObject;
import com.amay.tvm.coin.service.CoinModuleService;
import com.amay.tvm.coin.service.CoinResponseDecoder;
import com.amay.tvm.coin.service.HoppersRegistry;
import com.amay.tvm.coin.service.MaxChangePossibleService;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;

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
		haveAmount.totalAmount = amount;
		ReturnableAmountObject result = new MaxChangePossibleService().getReturnableAmount(new ReturnableAmountObject(new ArrayList<>()),haveAmount, amount, 0);
		return result.totalAmount == amount;
	}

	public boolean isDenominationPossibleAll(List<AmountDetail> list,int amount){
		HaveAmountObject haveAmountCoin = new HaveAmountObject(HoppersRegistry.INSTANCE.getHoppers());
		haveAmountCoin.totalAmount = amount;

		// Original test case - modified for standard denominations
		HaveAmountObject haveAmount = new HaveAmountObject(list);
		haveAmount.totalAmount = amount;

		HaveAmountObject combinedHaveAmount = new HaveAmountObject(new ArrayList<>());
		combinedHaveAmount.totalAmount = amount;
		combinedHaveAmount.amountDetailList.addAll(haveAmount.amountDetailList);
		combinedHaveAmount.amountDetailList.addAll(haveAmountCoin.amountDetailList);

		combinedHaveAmount.amountDetailList.sort((o1, o2) -> Integer.compare(o2.getAmount(), o1.getAmount())); // Sort in descending order of amount

		ReturnableAmountObject result = new MaxChangePossibleService().getReturnableAmount(new ReturnableAmountObject(new ArrayList<>()),combinedHaveAmount, amount, 0);
		result.amountDetailList.forEach(ad -> Logger.info("Using Denomination: {} x {} for", ad.getAmount(), ad.getQuantity(),amount));
		return result.totalAmount == amount;
	}

	public CoinResponseDecoder.CoinModuleDispenseResponse dispense(int amount){
		List<CoinResponseDecoder.DispenseResult> list=new ArrayList<>();
		CoinResponseDecoder.CoinModuleDispenseResponse dispenseResponse=new CoinResponseDecoder.CoinModuleDispenseResponse(false,0,"msg",list);;
		try{
		HaveAmountObject haveAmount = new HaveAmountObject(HoppersRegistry.INSTANCE.getHoppers());
		haveAmount.totalAmount = amount;
		ReturnableAmountObject result = new MaxChangePossibleService().getReturnableAmount(new ReturnableAmountObject(new ArrayList<>()),haveAmount, amount, 0);
		if(result.totalAmount!=amount && !service.isConnected()){
			return dispenseResponse;
		}

		for(AmountDetail amountDetail:result.amountDetailList) {
			int hopper= Integer.parseInt(amountDetail.getContainerId());
			ModuleResponse response= service.dispenseCoin((byte) hopper, (byte) amountDetail.quantity);
			System.out.println("Dispense done, DATA=" + response.getData().length + " bytes");
			CoinResponseDecoder.DispenseResult dispenseResult=CoinResponseDecoder.decodeDispenseResponse(response.getData());
			HoppersRegistry.INSTANCE.updateHopper(hopper,dispenseResult.quantityDispensed);
			list.add(dispenseResult);
		}

		boolean statue = false;  // NO NEED
		for(CoinResponseDecoder.DispenseResult dispenseResult:list){
			statue=statue || dispenseResult.success;
		}

		dispenseResponse.message="Dispense completed";
		dispenseResponse.success=statue;
		dispenseResponse.setTotalAmount();

		System.out.println("Dispense response: "+dispenseResponse);
		}catch (Exception e){
			Logger.error("ERROR DURING DISPENSE COINS : ",e.getMessage());
		}
		return dispenseResponse;
	}

	public ReturnableAmountObject getMaxChangeableAmount(HaveAmountObject haveAmountObject, long amount){
		ReturnableAmountObject result = new MaxChangePossibleService().getReturnableAmount(new ReturnableAmountObject(new ArrayList<>()),haveAmountObject, (int) amount, 0);
		System.out.println("Max changeable amount for "+amount+" is "+result.totalAmount);
		return result;

	}
}


