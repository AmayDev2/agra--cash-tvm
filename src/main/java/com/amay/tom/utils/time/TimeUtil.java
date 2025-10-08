package com.amay.tom.utils.time;

import com.amay.tom.config.TicketConfig;
import com.google.protobuf.Timestamp;
import org.tinylog.Logger;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class TimeUtil {
    public static String getCurrentTime() {
        return String.valueOf(System.currentTimeMillis());
    }

    public static long getCurrentTimeInLong() {
        return System.currentTimeMillis();
    }

    public static long getCurrentDateTimeInLong() {
        return new Date().getTime();
    }



    public static String getCurrentYearMonthDay() {
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();
        return ""+year+"-"+month+"-"+day;
    }

    public static String getDateMonth(long time) {
        LocalDateTime now = LocalDateTime.ofInstant(Instant.ofEpochMilli(time),ZoneId.systemDefault());
        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();
        return day+"-"+month;
    }

    public static String getCurrentWeekday(long time) {
        DayOfWeek dayOfWeek = LocalDateTime.ofInstant(Instant.ofEpochMilli(time),ZoneId.systemDefault()).getDayOfWeek();
        String day = dayOfWeek.toString(); // UPPERCASE like "MONDAY"

        if (day.equals("SUNDAY") || day.equals("SATURDAY")) {
            return day; // return "SUNDAY" or "SATURDAY"
        } else {
            return "WEEKDAYS";
        }
    }


    public static String getCurrentTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return now.format(formatter);
    }


    public static String getTime(String encodedDate, String encodedTime) {
        String date = decodeDate(encodedDate);
        String time = decodeTime(encodedTime);
        String dateTime= date + " " + time;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
        return formatter.format(localDateTime);

    }

    private static String decodeDate(String encodedDate) {

        if(encodedDate==null || encodedDate.length() != 6) {
            Logger.warn("Invalid encoded date: {}", encodedDate);
            throw new IllegalArgumentException("Invalid encoded date");
        }
        String year = "20" + encodedDate.substring(2, 4); // Assuming the year is in 21st century
        String month = encodedDate.substring(0, 2);
        String day = encodedDate.substring(4, 6);
        String date = year + "-" + month + "-" + day;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate localDate = LocalDate.parse(date, formatter);
        return formatter.format(localDate);
    }

    private static String decodeTime(String encodedTime) {
        if(encodedTime==null || encodedTime.length() != 4) {
        Logger.warn("Invalid encoded time: {}", encodedTime);
        throw new IllegalArgumentException("Invalid encoded time");
    }
// Decode the encoded time
        String hour = encodedTime.substring(0, 2);
        String minute = encodedTime.substring(2, 4);
        String time= hour + ":" + minute;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime localTime = LocalTime.parse(time, formatter);
        return formatter.format(localTime);
    }

    public static String addHours(String issueTime, int hours) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(issueTime, formatter);
        localDateTime = localDateTime.plusHours(hours);
        return localDateTime.format(formatter);
    }

    public static boolean isExpired(String expiryTime) {
        return true;
//        try{
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
//        LocalDateTime localDateTime = LocalDateTime.parse(expiryTime, formatter);
//        return LocalDateTime.now().isAfter(localDateTime);}
//        catch(DateTimeParseException e){
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
//            LocalDateTime localDateTime = LocalDateTime.parse(expiryTime, formatter);
//            return LocalDateTime.now().isAfter(localDateTime);
//        }catch (Exception e){
//            throw new IllegalArgumentException("Invalid expiry time");
//        }
    }

    public static boolean addedMinIsBeforeNow(String initiateDateTime, int ticketIssueToEntryTimeLimitMin) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(initiateDateTime, formatter);
        localDateTime = localDateTime.plusMinutes(ticketIssueToEntryTimeLimitMin);
        return LocalDateTime.now().isBefore(localDateTime);
    }

    public static long getTimeInMilli(String expiryTime) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            LocalDateTime localDateTime = LocalDateTime.parse(expiryTime, formatter);
            long milli = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            return milli;
        } catch (DateTimeParseException e) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            LocalDateTime localDateTime = LocalDateTime.parse(expiryTime, formatter);
            long milli = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            return milli;
        }catch (Exception e){
            throw new IllegalArgumentException("Invalid expiry time");
        }
    }

    public static long StringToTime(String initiateDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(initiateDateTime, formatter);
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static long addHoursEpoch(long issuanceTime, int ticketTime) {

        return issuanceTime + ticketTime*3600;


    }


    //Epoc to UTC Time
    private final static String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";

    public static String epochToFormattedTime(String issueTime, String format) {

        long epoch = Long.parseLong(issueTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format==null?DATE_FORMAT:format);
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(epoch, 0, ZoneOffset.UTC);
        return formatter.format(localDateTime);

    }



    //Epoc to System Time
        public static String epochToFormattedSystemTime(String issueTime, String format) {

            long epoch = Long.parseLong(issueTime);
            SimpleDateFormat formatter = new SimpleDateFormat(format==null?DATE_FORMAT:format);
            return formatter.format(new Date(epoch*1000));
        }
    public static String epochMilliToFormattedSystemTime(String issueTime, String format) {
//        if(format==null)format=DATE_FORMAT;

        long epoch = Long.parseLong(issueTime);
        SimpleDateFormat formatter = new SimpleDateFormat(format==null?DATE_FORMAT:format);
        return formatter.format(new Date(epoch));
    }



    public static String epochMilliToFormattedSystemTime(long epoch, String format) {

        SimpleDateFormat formatter = new SimpleDateFormat(format==null?DATE_FORMAT:format);
        return formatter.format(new Date(epoch));
    }


    public static long addMinEpoch(long issuanceTime, int ticketTime) {

        return issuanceTime + ticketTime * 60L;

    }


        // Convert Timestamp to LocalDateTime
        public static LocalDateTime timestampToLocalDateTime(Timestamp timestamp) {
            return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos()), ZoneId.systemDefault());
        }

        // Convert LocalDateTime to Timestamp
        public static Timestamp localDateTimeToTimestamp(LocalDateTime localDateTime) {
            Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
            return Timestamp.newBuilder().setSeconds(instant.getEpochSecond()).setNanos(instant.getNano()).build();
        }




    public static boolean isWithinRefundWindow(long epochMilliseconds) {
        long maxRefundTime = TicketConfig.INSTANT.getProductTypeDefDTO().getMaxRefundTime();

        Instant expiryTime;
        if (maxRefundTime == 0) {
            // Whole day means until 23:59:59.999 of the same day in system default time zone
            LocalDate date = Instant.ofEpochMilli(epochMilliseconds)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            expiryTime = date.atTime(LocalTime.MAX) // 23:59:59.999999999
                    .atZone(ZoneId.systemDefault())
                    .toInstant();
        } else {
            expiryTime = Instant.ofEpochMilli(epochMilliseconds).plusSeconds(maxRefundTime * 60);
        }

        Instant now = Instant.now();

        //System.out.println("Expiry Time: " + expiryTime);
        //System.out.println("Current Time: " + now);

        return expiryTime.isAfter(now);
    }


    public static String getCurrentDayPrefix() {
        String formate="yyMMdd";
        LocalDateTime currentTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        return currentTime.format(DateTimeFormatter.ofPattern(formate));
    }

    public static boolean isCurrentDay(String day) {
        String formate="yyMMdd";
        LocalDateTime currentTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        return currentTime.format(DateTimeFormatter.ofPattern(formate)).equals(day);
    }


    public static boolean compareForAnalysis(String initiateDateTime) {
        try {
            long epochMillis = Long.parseLong(initiateDateTime); // parse string to epoch milliseconds

            // Convert epoch millis to LocalDate
            LocalDate issueLocalDate = Instant.ofEpochMilli(epochMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            LocalDate currentLocalDate = Instant.now()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            if (issueLocalDate.equals(currentLocalDate)) {
                return true; // valid same-day
            } else {
                Logger.warn("Ticket expired: not issued today");
                return false;
            }
        } catch (Exception e) {
            Logger.warn("Ticket expired: ERROR {}", e.getMessage());
            return false;
        }
    }


    public static boolean compareForAnalysis(String ticketIssue, int ticketIssueToEntryTimeLimitMin) {
        try {
            long epochMillis = Long.parseLong(ticketIssue);
            Instant issueInstant = Instant.ofEpochMilli(epochMillis);
            Instant expiryInstant;

            if (ticketIssueToEntryTimeLimitMin == 0) {
                // Valid until the end of the same day
                LocalDate issueDate = issueInstant.atZone(ZoneId.systemDefault()).toLocalDate();
                expiryInstant = issueDate.atTime(LocalTime.MAX)
                        .atZone(ZoneId.systemDefault())
                        .toInstant();
            } else {
                // Valid for N minutes from ticket issue
                expiryInstant = issueInstant.plusSeconds(ticketIssueToEntryTimeLimitMin * 60L);
            }

            Instant now = Instant.now();

            if (expiryInstant.isBefore(now)) {
                return true;
            } else {
                Logger.warn("Ticket expired: current time exceeds validity");
                return false;
            }

        } catch (Exception e) {
            Logger.warn("Ticket check failed: ERROR {}", e.getMessage());
            return false;
        }
    }


    public static long getCurrentMilli() {
        Instant now = Instant.now();
        return now.toEpochMilli();
    }

    public static String epochMilliToFormattedSystemLocalDateTime(long transactionTimeEpoch, String format) {
        // Convert epoch millis to LocalDateTime in system default zone
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(transactionTimeEpoch),
                ZoneId.systemDefault()
        );

        SimpleDateFormat formatter = new SimpleDateFormat(format==null?DATE_FORMAT:format);
        return formatter.format(new Date(transactionTimeEpoch));


    }

    public static LocalDateTime getCurrentTimeAsLocalDateTime() {
        return LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
    }

    public static String formated(LocalDateTime localDateTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        return localDateTime.format(formatter);
    }
}
