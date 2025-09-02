package com.amay.tom.service.events.commands;

import com.amay.tom.listener.ModesListener;
import com.amay.tom.service.events.TOMCommand;
import com.amay.tom.service.tom.IApplicationService;
import org.tinylog.Logger;

public class EmergencyCommand implements TOMCommand {

    private final IApplicationService applicationService;

    public EmergencyCommand(IApplicationService applicationService) {
        Logger.debug("DeviceInfoCommand constructor {}", applicationService);
        this.applicationService = applicationService;
    }
    @Override
    public boolean executeCommand() {
        Logger.debug("DeviceInfoCommand executeCommand {}", applicationService);
          applicationService.notifyListener(ModesListener.class, EmergencyCommand.class);

          return true;

                 //.sendDeviceInfo();

    }
}
