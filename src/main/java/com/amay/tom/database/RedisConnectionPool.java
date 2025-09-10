package com.amay.tom.database;
import com.amay.tom.utils.env.EnvFile;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

public class RedisConnectionPool {
    private static final String REDIS_HOST = EnvFile.getRedisHost();
    private static final int REDIS_PORT = EnvFile.getRedisPort();
    private static final String REDIS_PASSWORD = EnvFile.getRedisPassword();

    private static final JedisPool jedisPool;

    static {
        // Configure the JedisPoolConfig
        JedisPoolConfig poolConfig = new JedisPoolConfig();

        poolConfig.setMaxTotal(5); // Maximum number of connections in the pool
        poolConfig.setMaxIdle(3); // Maximum number of idle connections in the pool
        poolConfig.setMinIdle(1); // Minimum number of idle connections in the pool
        poolConfig.setTestOnBorrow(true); // Test the connection before borrowing it from the pool

        //System.out.println("REDIS_HOST: " + REDIS_HOST);

        // Create the JedisPool
        jedisPool = new JedisPool(poolConfig, REDIS_HOST, REDIS_PORT, 2000, REDIS_PASSWORD);
    }

    public static JedisPool getJedisPool() {
        return jedisPool;
    }

    public static void closeJedisPool() {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }

    public static boolean isRedisAlive() {
        try (Jedis jedis = jedisPool.getResource()) {
            String response = jedis.ping();
            return "PONG".equals(response);
        } catch (JedisException e) {
            return false;
        }
    }

    public static void totalConnections() {
        //System.out.println("Total connections: " + jedisPool.getNumActive());
    }
}
