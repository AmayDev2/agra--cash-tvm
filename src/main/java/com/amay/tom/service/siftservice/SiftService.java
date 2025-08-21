package com.amay.tom.service.siftservice;

import com.amay.tom.enums.EOSType;
import com.amay.tom.model.siftdata.User;

public interface SiftService {
public void startShift(User user);
public void endOfShift(EOSType eosType);
public void pauseShift();
public void resumeShift();
}
