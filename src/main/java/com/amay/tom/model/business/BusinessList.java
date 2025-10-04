package com.amay.tom.model.business;

import com.amay.tom.config.dto.BusinessDayConfigDTO;
import com.amay.tom.config.dto.CalendarConfigDTO;
import com.amay.tom.config.dto.PeakTimeConfigDTO;
import com.amay.tom.utils.time.TimeUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BusinessList {
    private List<CalendarConfigDTO> specialDayCalender;
    private List<BusinessDayConfigDTO> businessDays;
    private List<PeakTimeConfigDTO> peakTimes;

    @JsonIgnore
    private BusinessDayConfigDTO today;

    @JsonIgnore
    public boolean isActiveWorkingHour(){
        if(null==today)this.today=today();
        return null == today || isCurrentTimeBetween(this.today.getStartTime(), this.today.getEndTime());
    }

    @JsonIgnore
    public long getRemainingSecondsOfWorkingHour(){
        if(null==today)this.today=today();
        if(null==today)return 0;
        return getRemainingSecondsOfWorkingHour(this.today.getStartTime(),this.today.getEndTime());
    }


    @JsonIgnore
    public BusinessDayConfigDTO today() {
        String time=TimeUtil.getDateMonth(Instant.now().toEpochMilli());
        String day=TimeUtil.getCurrentWeekday(Instant.now().toEpochMilli());

        //weekend
        Optional<BusinessDayConfigDTO> todayOptional1 = businessDays.stream()
                        .filter(x -> day.equals(x.getDayType()))
                        .findFirst();
        today = todayOptional1.orElse(null);

        //special
        for(CalendarConfigDTO calendarConfig :specialDayCalender){
            if(calendarConfig.getSpecialDate().equals(time)){
                Optional<BusinessDayConfigDTO> todayOptional = businessDays.stream()
                        .filter(x -> "SPECIALDAY".equals(x.getDayType()))
                        .findFirst();
                today = todayOptional.orElse(null);
            }
        }
        return today;
    }

    @JsonIgnore
    public BusinessDayConfigDTO givenDay(long dateTime) {
        String time=TimeUtil.getDateMonth(dateTime);
        String day=TimeUtil.getCurrentWeekday(dateTime);

        //weekend
        Optional<BusinessDayConfigDTO> todayOptional1 = businessDays.stream()
                .filter(x -> day.equals(x.getDayType()))
                .findFirst();
        BusinessDayConfigDTO today = todayOptional1.orElse(null);

        //special
        for(CalendarConfigDTO calendarConfig :specialDayCalender){
            if(calendarConfig.getSpecialDate().equals(time)){
                Optional<BusinessDayConfigDTO> todayOptional = businessDays.stream()
                        .filter(x -> "SPECIALDAY".equals(x.getDayType()))
                        .findFirst();
                today = todayOptional.orElse(null);
            }
        }
        return today;
    }
    // get today

    // get today's fare multiplayer
        //1- special day -> day multiplayer
        // 2- weekday -> peak time multiplayer

    @JsonIgnore
    public double getFareMultiplayer(){
        if(null==today)this.today=today();
        if(this.today!=null && this.today.getDayType().equals("WEEKDAYS") ){
            Optional<PeakTimeConfigDTO> peakTime = peakTimes.stream().filter(x-> isCurrentTimeBetween(x.getStartTime(),x.getEndTime()))
                    .findFirst();
            if(peakTime.isPresent()){
                return peakTime.get().getFareMultiplier();
            }
        }
        return null==this.today?1:this.today.getFareMultiplier();
    }

    @JsonIgnore
    public double getFareMultiplayer(long ticketIssueTime){
        BusinessDayConfigDTO today=givenDay(ticketIssueTime);
        if(today!=null && today.getDayType().equals("WEEKDAYS") ){
            Optional<PeakTimeConfigDTO> peakTime = peakTimes.stream().filter(x-> isGivenTimeBetween(x.getStartTime(),x.getEndTime(),ticketIssueTime))
                    .findFirst();
            if(peakTime.isPresent()){
                return peakTime.get().getFareMultiplier();
            }
        }
        return null==today?1:today.getFareMultiplier();
    }

    @JsonIgnore
    public static boolean isCurrentTimeBetween(String startTimeStr, String endTimeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime now = LocalTime.now();
        LocalTime startTime = LocalTime.parse(startTimeStr, formatter);
        LocalTime endTime = LocalTime.parse(endTimeStr, formatter);
        return !now.isBefore(startTime) && !now.isAfter(endTime);
    }

    @JsonIgnore
    public static long getRemainingSecondsOfWorkingHour(String startTimeStr, String endTimeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime now = LocalTime.now();
        LocalTime startTime = LocalTime.parse(startTimeStr, formatter);
        LocalTime endTime = LocalTime.parse(endTimeStr, formatter);

//        // If current time is before start, consider full working hour remaining
        if (now.isBefore(startTime)) {
            return Duration.between(startTime, endTime).getSeconds();
        }

        // If current time is after end, 0 seconds remain
        if (now.isAfter(endTime)) {
            return 0;
        }

        // Otherwise, return remaining seconds from now to end
        return Duration.between(now, endTime).getSeconds();
    }


    @JsonIgnore
    public static boolean isGivenTimeBetween(String startTimeStr, String endTimeStr,long time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime now = LocalTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
        LocalTime startTime = LocalTime.parse(startTimeStr, formatter);
        LocalTime endTime = LocalTime.parse(endTimeStr, formatter);
        return !now.isBefore(startTime) && !now.isAfter(endTime);
    }


    @JsonIgnore
    public boolean isUnderPeakTime(long ticketIssueTime) {
        if(null==today)this.today=today();
        boolean isPeakTime = false;
        if(this.today!=null && this.today.getDayType().equals("WEEKDAYS") ){
            Optional<PeakTimeConfigDTO> peakTime = peakTimes.stream().filter(x-> isGivenTimeBetween(x.getStartTime(),x.getEndTime(),ticketIssueTime))
                    .findFirst();
            if(peakTime.isPresent()){
                isPeakTime= true;
            }
        }
        return isPeakTime;
    }
}
