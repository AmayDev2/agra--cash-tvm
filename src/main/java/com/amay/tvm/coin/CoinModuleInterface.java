package com.amay.tvm.coin;


import com.amay.tvm.coin.model.AmountDetail;
import com.amay.tvm.coin.model.HaveAmountObject;
import com.amay.tvm.coin.model.ModuleResponse;
import com.amay.tvm.coin.model.ReturnableAmountObject;
import com.amay.tvm.coin.service.CoinModuleService;
import com.amay.tvm.coin.service.CoinResponseDecoder;
import com.amay.tvm.coin.service.HoppersRegistry;
import com.amay.tvm.coin.service.MaxChangePossibleService;

import java.util.ArrayList;
import java.util.List;

public enum CoinModuleInterface {
	INSTANCE;
	CoinModuleService service;
	public CoinModuleService setupCoinModule(String comPort){
		service = new CoinModuleService();
		service.connect(comPort);
		HoppersRegistry.INSTANCE.setHoppers(8,10,0);
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

	public boolean dispense(int amount){
		try{
		HaveAmountObject haveAmount = new HaveAmountObject(HoppersRegistry.INSTANCE.getHoppers());
//		haveAmount.amountDetailList.addAll();
		haveAmount.totalAmount = amount;
		ReturnableAmountObject result = new MaxChangePossibleService().getReturnableAmount(new ReturnableAmountObject(new ArrayList<>()),haveAmount, amount, 0);
		if(result.totalAmount!=amount && !service.isConnected()){
			return false;
		}

		List<CoinResponseDecoder.DispenseResult> list=new ArrayList<>();
		for(AmountDetail amountDetail:result.amountDetailList) {
			int hopper=getHopper(amountDetail.amount);
			ModuleResponse response= service.dispenseCoin((byte) hopper, (byte) amountDetail.quantity);
			System.out.println("Dispense done, DATA=" + response.getData().length + " bytes");
			CoinResponseDecoder.DispenseResult dispenseResult=CoinResponseDecoder.decodeDispenseResponse(response.getData());
			HoppersRegistry.INSTANCE.updateHopper(hopper,dispenseResult.amountDispensed);
			list.add(dispenseResult);
		}
		boolean statue = true;
		for(CoinResponseDecoder.DispenseResult dispenseResult:list){
			statue=statue || dispenseResult.success;
		}
		return statue;
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}

	private int getHopper(int amount) {
		if(amount==10){
			return 2;
		}else if(amount==5){
			return 1;
		}else{
			return 0;
		}
	}


	public ReturnableAmountObject getMaxChangeableAmount(HaveAmountObject haveAmountObject, long amount){
		ReturnableAmountObject result = new MaxChangePossibleService().getReturnableAmount(new ReturnableAmountObject(new ArrayList<>()),haveAmountObject, (int) amount, 0);
		System.out.println("Max changeable amount for "+amount+" is "+result.totalAmount);
		return result;

	}
}


