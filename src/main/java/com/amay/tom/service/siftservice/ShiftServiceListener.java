package com.amay.tom.service.siftservice;

import com.amay.tom.enums.EOSType;
import com.amay.tom.service.events.commands.EOSCommand;
import com.amay.tom.service.tom.RemoteListener;

import javax.management.ServiceNotFoundException;
import java.util.Arrays;

public class ShiftServiceListener implements RemoteListener, InternalListener {

    private final com.amay.tom.service.siftservice.ShiftService shiftService;

    public ShiftServiceListener(com.amay.tom.service.siftservice.ShiftService shiftService){
        this.shiftService=shiftService;
    }

    @Override
    public void sunleBhai(Class<?> commandClass) throws ServiceNotFoundException {
        if(null==shiftService){
            throw new ServiceNotFoundException(ShiftService.class+" instance not found in listener: "+ShiftServiceListener.class);
        }
        if(commandClass.equals(EOSCommand.class)){
            shiftService.endOfShift(EOSType.OPERATOR);
        }else{
            throw new ServiceNotFoundException("Service not found");
        }
    }

    @Override
    public void PauseShift() {
        shiftService.pauseShift();
    }

    @Override
    public void EOShift() {
        shiftService.endOfShift(EOSType.OPERATOR);
    }

    @Override
    public void ResumeShift(char[] password) {
        shiftService.resumeShift(Arrays.toString(password));
    }
}
