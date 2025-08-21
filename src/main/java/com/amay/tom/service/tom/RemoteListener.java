package com.amay.tom.service.tom;

import javax.management.ServiceNotFoundException;

public interface RemoteListener {
    public void sunleBhai(Class<?> commandClass) throws ServiceNotFoundException;

}
