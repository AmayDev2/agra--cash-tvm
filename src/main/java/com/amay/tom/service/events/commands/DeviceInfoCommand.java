package com.amay.tom.service.events.commands;

import com.amay.tom.grpc.monotoring.GrpcApiListener;
import com.amay.tom.service.events.TOMCommand;
import com.amay.tom.service.tom.IApplicationService;
import org.tinylog.Logger;

public class DeviceInfoCommand implements TOMCommand {

    private final IApplicationService applicationService;

    public  DeviceInfoCommand(IApplicationService applicationService) {
        Logger.debug("DeviceInfoCommand constructor {}", applicationService);
        this.applicationService = applicationService;
    }
    @Override
    public boolean executeCommand() {
        Logger.debug("DeviceInfoCommand executeCommand {}", applicationService);
          applicationService.notifyListener(GrpcApiListener.class,DeviceInfoCommand.class);
          return true;

                 //.sendDeviceInfo();

    }
}
