package com.cy.config.cache;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author cypei
 * @date 2022/7/7
 */
@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient(RedisProperties redisProperties) {
        //创建配置
        Config config = new Config();
        /**
         *  连接哨兵：config.useSentinelServers().setMasterName("myMaster").addSentinelAddress()
         *  连接集群： config.useClusterServers().addNodeAddress()
         */
        final String host = redisProperties.getHost();
        final int port = redisProperties.getPort();
        final String password = redisProperties.getPassword();
        final int database = redisProperties.getDatabase();
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setTimeout(5000)
                .setDatabase(database)
                .setPassword(password);
        
        //根据config创建出RedissonClient实例
        return Redisson.create(config);
    }

}
