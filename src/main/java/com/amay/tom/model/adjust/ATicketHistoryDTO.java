package com.amay.tom.model.adjust;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
@RequiredArgsConstructor
@Accessors
public class ATicketHistoryDTO {
    private String ticketId;
    private int entryAllowed;
    private int entryCount;
    private long lastEntryTime;
    private int exitAllowed;
    private int exitCount;
    private long lastExitTime;
    private int adjustCount;
    private int adjustedEntryAllowed;
    private int adjustedEntryCount;
    private int adjustedExitAllowed;
    private int adjustedExitCount;
    private boolean overtimeOverride;
    private boolean overtravelOverride;

}
