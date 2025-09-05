package com.amay.tom.model.session;

import java.time.LocalDateTime;

public class ShiftReport {
    // Ticket details
    int noOfSJT = 0, qSJT = 0, amountSJT = 0;
    int noOfRJT = 0, qRJT = 0, amountRJT = 0;
    int noOfGroup = 0, qGroup = 0, amountGroup = 0;
    int noOfFree = 0, qFree = 0, amountFree = 0;
    int noOfPaid = 0, qPaid = 0, amountPaid = 0;
    int noOfCanceled = 0, qCanceled = 0, amountCanceled = 0;
    int noOfAdjusted = 0, qAdjusted = 0, amountAdjusted = 0;
    int noOfReplaced = 0, qReplaced = 0, amountReplaced = 0;


    // New variables for operator details
    String operatorId = "";
    String operatorName = "";
    String operatorContact = "";

    String Line = "";
    String station="";

    // New variables for operator details
    String SLESerial = "";
    String SLEId = "";
    String SLEName = "";
    String SLEType = "";

    // Shift details
    String shiftId = ""; // ID of the shift
    LocalDateTime shiftStartTime = null; // Start time of the shift
    LocalDateTime shiftEndTime = null; // End time of the shift

    // Additional fields
    LocalDateTime lastTransaction = null;
    int totalAmount = 0;

}
