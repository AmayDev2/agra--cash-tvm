package com.amay.tom.grpc.monotoring;

import com.amay.tom.service.events.TOMCommand;
import com.amay.tom.service.tom.IApplicationService;

public class AppCloseCommand implements TOMCommand {
    private final IApplicationService applicationService;
    public AppCloseCommand(IApplicationService applicationService) {
        this.applicationService=applicationService;
    }

    @Override
    public boolean executeCommand() {
        this.applicationService.appClose();
        return false;
    }
}
