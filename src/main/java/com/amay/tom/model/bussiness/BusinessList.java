package com.amay.tom.model.bussiness;

import com.amay.tom.utils.time.TimeUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

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
    private List<CalenderConfig> specialDayCalender;
    private List<BusinessDayConfig> businessDays;
    private List<PeakTimeConfig> peakTimes;

    @JsonIgnore
    private BusinessDayConfig today;

    @JsonIgnore
    public boolean isActiveWorkingHour(){
        if(null==today)this.today=today();
        return null == today || isCurrentTimeBetween(this.today.getStartTime(), this.today.getEndTime());
    }


    @JsonIgnore
    public BusinessDayConfig today() {
        String time=TimeUtil.getDateMonth(Instant.now().toEpochMilli());
        String day=TimeUtil.getCurrentWeekday(Instant.now().toEpochMilli());

        //weekend
        Optional<BusinessDayConfig> todayOptional1 = businessDays.stream()
                        .filter(x -> day.equals(x.getDayType()))
                        .findFirst();
        today = todayOptional1.orElse(null);

        //special
        for(CalenderConfig calenderConfig:specialDayCalender){
            if(calenderConfig.getSpecialDate().equals(time)){
                Optional<BusinessDayConfig> todayOptional = businessDays.stream()
                        .filter(x -> "SPECIALDAY".equals(x.getDayType()))
                        .findFirst();
                today = todayOptional.orElse(null);
            }
        }
        return today;
    }

    @JsonIgnore
    public BusinessDayConfig givenDay(long dateTime) {
        String time=TimeUtil.getDateMonth(dateTime);
        String day=TimeUtil.getCurrentWeekday(dateTime);

        //weekend
        Optional<BusinessDayConfig> todayOptional1 = businessDays.stream()
                .filter(x -> day.equals(x.getDayType()))
                .findFirst();
        BusinessDayConfig today = todayOptional1.orElse(null);

        //special
        for(CalenderConfig calenderConfig:specialDayCalender){
            if(calenderConfig.getSpecialDate().equals(time)){
                Optional<BusinessDayConfig> todayOptional = businessDays.stream()
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
            Optional<PeakTimeConfig> peakTime = peakTimes.stream().filter(x-> isCurrentTimeBetween(x.getStartTime(),x.getEndTime()))
                    .findFirst();
            if(peakTime.isPresent()){
                return peakTime.get().getFareMultiplier();
            }
        }
        return null==this.today?1:this.today.getFareMultiplier();
    }

    @JsonIgnore
    public double getFareMultiplayer(long ticketIssueTime){
        BusinessDayConfig today=givenDay(ticketIssueTime);
        if(today!=null && today.getDayType().equals("WEEKDAYS") ){
            Optional<PeakTimeConfig> peakTime = peakTimes.stream().filter(x-> isGivenTimeBetween(x.getStartTime(),x.getEndTime(),ticketIssueTime))
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
    public static boolean isGivenTimeBetween(String startTimeStr, String endTimeStr,long time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime now = LocalTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
        LocalTime startTime = LocalTime.parse(startTimeStr, formatter);
        LocalTime endTime = LocalTime.parse(endTimeStr, formatter);
        return !now.isBefore(startTime) && !now.isAfter(endTime);
    }
}
