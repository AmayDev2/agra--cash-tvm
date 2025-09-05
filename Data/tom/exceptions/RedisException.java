package com.amay.tom.exceptions;

import com.amay.tom.config.SystemConfig;
import org.tinylog.Logger;

public class RedisException extends RuntimeException{

    public RedisException(String message) {
        super(message);
        SystemConfig.getInstance().setRedisStatus(false);
        Logger.error( message);
    }

    public RedisException(String message, Throwable cause) {
        super(message, cause);
        SystemConfig.getInstance().setRedisStatus(false);
        Logger.error(cause, message);
    }

    public RedisException(Throwable cause) {
        super(cause);
        SystemConfig.getInstance().setRedisStatus(false);
    }

    public RedisException() {
        super();
    }
}
