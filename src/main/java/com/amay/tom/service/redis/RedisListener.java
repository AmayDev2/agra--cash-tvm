package com.amay.tom.service.redis;

import com.amay.tom.config.SystemConfig;
import com.amay.tom.enums.Channels;
import com.amay.tom.enums.Command;
import com.amay.tom.exceptions.RedisException;
import com.amay.tom.model.RedisMessage;
import com.amay.tom.service.events.Remote;
import com.amay.tom.service.events.TOMCommand;
import com.amay.tom.service.events.commands.EOSCommand;
import com.amay.tom.service.tom.ApplicationService;
import com.amay.tom.service.tom.IApplicationService;
import com.amay.tom.utils.env.EnvFile;
import com.amay.tom.utils.helper.Helper;
import org.tinylog.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

public class RedisListener {

    private  JedisPool jedisPool = null;

    private final String THREAD_NAME = "RedisListenerThread";

    private IApplicationService applicationService;

    private String threadName;

    Remote remote;


    public RedisListener(IApplicationService applicationService,JedisPool jedisPool) {
        try {
            this.jedisPool = jedisPool;
            this.applicationService = applicationService;
            Logger.info("Connected to Redis server {}",  1);

        } catch (Exception e) {
            throw new RedisException("Error connecting to Redis server", e);

        }
    }

    private void subscribeToChannels(String... channels){
        Logger.info("Subscribing to channels: {}", channels);
        jedisPool.getResource().subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                try{
                Channels channelEnum = Channels.valueOf(channel);
                Logger.info("Received message from channel: {}  {}", channel, message);
                switch (channelEnum) {
                    case COMMAND_CHANNEL:
                                sendCommand(message);
//                        System.out.println("Received command: " + message + "\n");
                        break;
                    case NOTIFICATION_CHANNEL:
                        System.out.println("Received notification: " + message + "\n");
                        break;
                    default:
                        System.out.println("Received message from " + channel + ": " + message + "\n");
                        break;
                }
                } catch (Exception e) {
                    throw new RedisException("Error processing message ", e);
                }
            }

            @Override
            public void onSubscribe(String channel, int subscribedChannels) {
                //Platform.runLater(() -> messageArea.appendText("Subscribed to channel: " + channel + "\n"));
            }

            @Override
            public void onUnsubscribe(String channel, int subscribedChannels) {
                //Platform.runLater(() -> messageArea.appendText("Unsubscribed from channel: " + channel + "\n"));
            }

            @Override
            public void ping() {
                   System.out.println("Ping received");
            }

        }, channels);

    }

    public void startSCUEventListener(String... channels) {
       new Thread(() -> {
            try {
                subscribeToChannels(channels);
            } catch (Exception e) {
                e.getStackTrace();
            }
        },THREAD_NAME).start();


    }


    private void sendCommand(String message) {

        System.out.println("Received command: " + RedisMessage.class + "\n");

        RedisMessage redisMessage= (RedisMessage) Helper.JSONtoObject(message, RedisMessage.class);
        assert redisMessage != null;
        Command command =Command.valueOf(redisMessage.getMessage());
        TOMCommand tomCommand ;
        switch(command){
            case END_OF_SHIFT :
               remote= new Remote(new EOSCommand(applicationService));
                break;
            case EMERGENCY_MODE:
//                tomCommand = new (applicationService);
                break;
            case PAUSE_SHIFT:
//                remote.endOfDay();
                break;
            case NORMAL_MODE:
//                remote.normalMode();
                break;
            default:
                System.out.println("Received command: " + message + "\n");
        }

        remote.pressButton();


    }







}
