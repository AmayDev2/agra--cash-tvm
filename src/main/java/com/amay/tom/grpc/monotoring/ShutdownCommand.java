package com.amay.tom.grpc.monotoring;

import com.amay.tom.service.events.TOMCommand;
import com.amay.tom.service.tom.IApplicationService;

public class ShutdownCommand implements TOMCommand {
    private final IApplicationService applicationService;
    public ShutdownCommand(IApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @Override
    public boolean executeCommand() {
        return this.applicationService.systemShutdown();
    }
}
