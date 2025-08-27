package com.amay.tom.service.events.commands;

import com.amay.tom.listener.ModesListener;
import com.amay.tom.service.events.TOMCommand;
import com.amay.tom.service.tom.IApplicationService;

public class InServiceNoSaleCommand implements TOMCommand {

    private IApplicationService applicationService;

    public InServiceNoSaleCommand(IApplicationService applicationService) {
        this.applicationService = applicationService;
    }
    @Override
    public boolean executeCommand() {
        applicationService.notifyListener(ModesListener.class, InServiceNoSaleCommand.class);
        return true;

    }
}
