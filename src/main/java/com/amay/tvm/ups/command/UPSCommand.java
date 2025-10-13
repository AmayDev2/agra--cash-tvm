package com.amay.tvm.ups.command;

/**
 * UPS Command definitions based on protocol specification
 */
public enum UPSCommand {
    STATUS_INQUIRY("Q1", "Status Inquiry"),
    TEST_10_SECONDS("T", "Test for 10 seconds"),
    TEST_UNTIL_BATTERY_LOW("TL", "Test until battery low"),
    CANCEL_TEST("CT", "Cancel test command"),
    CANCEL_SHUTDOWN("C", "Cancel shutdown command"),
    UPS_INFORMATION("I", "UPS information command");

    private final String command;
    private final String description;

    UPSCommand(String command, String description) {
        this.command = command;
        this.description = description;
    }

    public String getCommand() {
        return command;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return command;
    }

    /**
     * Test for specified time period (01-99 minutes)
     */
    public static String testForMinutes(int minutes) {
        if (minutes < 1 || minutes > 99) {
            throw new IllegalArgumentException("Minutes must be between 01 and 99");
        }
        return String.format("T%02d", minutes);
    }

    /**
     * Shutdown command (01-10 minutes)
     */
    public static String shutdown(int minutes) {
        if (minutes < 1 || minutes > 10) {
            throw new IllegalArgumentException("Shutdown minutes must be between 01 and 10");
        }
        return String.format("S%02d", minutes);
    }

    /**
     * Shutdown and restore command
     * @param shutdownMinutes 01-10 minutes
     * @param restoreMinutes 0001-9999 minutes
     */
    public static String shutdownAndRestore(int shutdownMinutes, int restoreMinutes) {
        if (shutdownMinutes < 1 || shutdownMinutes > 10) {
            throw new IllegalArgumentException("Shutdown minutes must be between 01 and 10");
        }
        if (restoreMinutes < 1 || restoreMinutes > 9999) {
            throw new IllegalArgumentException("Restore minutes must be between 0001 and 9999");
        }
        return String.format("S%02dR%04d", shutdownMinutes, restoreMinutes);
    }
}
