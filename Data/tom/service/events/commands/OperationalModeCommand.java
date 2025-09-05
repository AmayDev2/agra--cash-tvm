package com.amay.tom.service.events.commands;

import com.amay.tom.service.events.TOMCommand;
import com.amay.tom.service.tom.IApplicationService;

public class OperationalModeCommand implements TOMCommand {

    private IApplicationService applicationService;

    public OperationalModeCommand(IApplicationService applicationService) {
        this.applicationService = applicationService;
    }
    @Override
    public boolean executeCommand() {
         return applicationService.sendCurrentMode();

    }
}
