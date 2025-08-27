package com.amay.tom.utils.ticket;

import com.amay.tom.utils.time.TimeUtil;

public class TicketUtil {

    public static boolean isTodayTicket(long issueAt) {
        // checking id date are not same
        long currentTime = TimeUtil.getCurrentMilli();
        // get date of both issueAt and currentTime
        String issueAtDate = TimeUtil.getDateMonth(issueAt);
        String currentDate = TimeUtil.getDateMonth(currentTime);
        // if both dates are same then return true
        return issueAtDate.equals(currentDate);
    }
}
