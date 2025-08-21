package com.amay.tom.service.events.commands;

import com.amay.tom.listener.ModesListener;
import com.amay.tom.service.events.TOMCommand;
import com.amay.tom.service.tom.IApplicationService;

public class InServiceBothCommand implements TOMCommand {

    private IApplicationService applicationService;

    public InServiceBothCommand(IApplicationService applicationService) {
        this.applicationService = applicationService;
    }
    @Override
    public boolean executeCommand() {
        applicationService.notifyListener(ModesListener.class, InServiceBothCommand.class);
        return true;

    }
}
