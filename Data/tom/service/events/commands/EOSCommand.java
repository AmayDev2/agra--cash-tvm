package com.amay.tom.service.events.commands;

import com.amay.tom.service.events.TOMCommand;
import com.amay.tom.service.siftservice.ShiftServiceListener;
import com.amay.tom.service.tom.IApplicationService;

public class EOSCommand implements TOMCommand {

    private final IApplicationService applicationService;

    public EOSCommand(IApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @Override
    public boolean executeCommand() {
        applicationService.notifyListener(ShiftServiceListener.class,EOSCommand.class);
        return true;
    }
}
