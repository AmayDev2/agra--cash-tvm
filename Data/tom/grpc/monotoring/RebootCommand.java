package com.amay.tom.grpc.monotoring;

import com.amay.tom.service.events.TOMCommand;
import com.amay.tom.service.tom.IApplicationService;

public class RebootCommand implements TOMCommand {
    private final IApplicationService applicationService;
    public RebootCommand(IApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @Override
    public boolean executeCommand() {
        return this.applicationService.systemReboot();
    }
}
