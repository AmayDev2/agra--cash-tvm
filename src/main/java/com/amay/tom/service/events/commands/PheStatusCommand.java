package com.amay.tom.service.events.commands;

import com.amay.tom.grpc.monotoring.GrpcApiListener;
import com.amay.tom.service.events.TOMCommand;
import com.amay.tom.service.tom.IApplicationService;

public class PheStatusCommand implements TOMCommand {

    private IApplicationService applicationService;

    public PheStatusCommand(IApplicationService applicationService) {
        this.applicationService = applicationService;
    }
    @Override
    public boolean executeCommand() {
          applicationService.notifyListener(GrpcApiListener.class, this.getClass());
            return true;

    }
}
