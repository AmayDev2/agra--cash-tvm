package com.amay.tom.service.events.commands;

import com.amay.tom.service.events.TOMCommand;
import com.amay.tom.service.tom.IApplicationService;

public class ModeCommand implements TOMCommand {

    private IApplicationService applicationService;

    public ModeCommand(IApplicationService applicationService) {
        this.applicationService = applicationService;
    }
    @Override
    public boolean executeCommand() {
         return applicationService.setMode();

    }
}
