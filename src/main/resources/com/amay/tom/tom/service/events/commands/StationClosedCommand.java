package com.amay.tom.service.events.commands;

import com.amay.tom.listener.ModesListener;
import com.amay.tom.service.events.TOMCommand;
import com.amay.tom.service.tom.IApplicationService;
import org.tinylog.Logger;

public class StationClosedCommand implements TOMCommand {

    private final IApplicationService applicationService;

    public StationClosedCommand(IApplicationService applicationService) {
        Logger.debug("DeviceInfoCommand constructor {}", applicationService);
        this.applicationService = applicationService;
    }
    @Override
    public boolean executeCommand() {
        Logger.debug("DeviceInfoCommand executeCommand {}", applicationService);
          applicationService.notifyListener(ModesListener.class, StationClosedCommand.class);

          return true;

                 //.sendDeviceInfo();

    }
}
