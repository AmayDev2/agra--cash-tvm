package com.amay.tom.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RedisMessage {
    private String deviceId;
    private String message;
    private String sender;

}
