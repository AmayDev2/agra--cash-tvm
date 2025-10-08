package com.amay.tom.service.events.commands;

import com.amay.tom.grpc.monotoring.GrpcApiListener;
import com.amay.tom.service.events.TOMCommand;
import com.amay.tom.service.tom.IApplicationService;
import org.tinylog.Logger;

public class OperationalModeCommand implements TOMCommand {

    private IApplicationService applicationService;

    public OperationalModeCommand(IApplicationService applicationService) {
        this.applicationService = applicationService;
    }
    @Override
    public boolean executeCommand() {
        Logger.debug("DeviceInfoCommand executeCommand {}", applicationService);
        applicationService.notifyListener(GrpcApiListener.class, OperationalModeCommand.class);
        return true;

    }
}
