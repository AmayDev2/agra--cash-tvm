package com.amay.tom.ipstatus;


import com.amay.tom.ipstatus.IpService;
import com.amay.tom.ipstatus.model.IpData;
import javafx.application.Platform;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ipServiceImpl implements IpService {
    List<IpData> ipList=new ArrayList<>();


    public void addIp(IpData ipData){
        ipList.add(ipData);
        //TODO: update in sqlite
    }

    public void removeIp(IpData ipData){
//        ipList.stream().findAny().filter(ipData1 -> ipData1.getIpAddress().equals(ipData.getIpAddress()));
        //TODO: delete from list

        //TODO: update the list
    }


    private void updateIps(){
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(
                        javafx.util.Duration.seconds(1),
                        event -> {
//                            LocalDateTime now = LocalDateTime.now();


                            Platform.runLater(() -> {
//                                timeLabel.setText(timeFormatter.format(now));
//                                dateLabel.setText(dateFormatter.format(now));
                            });
                        }
                )
        );
    }



}
