package com.github.xjs.ezjedis.config;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.github.xjs.ezjedis.service.JedisClient;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

/**
 * @author 605162215@qq.com
 *
 * @date 2018年7月17日 上午9:06:11<br/>
 */
@Configuration
@ComponentScan(basePackages = 
{ 
"com.github.xjs.ezjedis.config", 
"com.github.xjs.ezjedis.interceptor",
"com.github.xjs.ezjedis.service" })
//@ConditionalOnClass(Jedis.class)
public class EzJedisAutoConfiguration {
	
	private static final Logger log = LoggerFactory.getLogger(EzJedisAutoConfiguration.class);
	
	@Autowired
	EzJedisProperties properties;
	
	@Bean
	//@ConditionalOnMissingBean
	public JedisClient JedisClientFactory() {
		String hosts = properties.getHosts();
		int timeout = properties.getTimeout();
		String password =  properties.getPassword();
		if(StringUtils.isEmpty(password)) {
			properties.setPassword(null);
		}
		int poolMaxTotal = properties.getPoolMaxTotal();
		int poolMaxIdle = properties.getPoolMaxIdle();
		int poolMaxWait = properties.getPoolMaxWait();
		String sentinelMasterName = properties.getSentinelMasterName();
		String hostPorts[] = hosts.split(",");
		if(!StringUtils.isEmpty(sentinelMasterName)) {//sentinel
			log.info("JedisSentinelPool创建！hosts:"+hosts+",poolMaxTotal:"+poolMaxTotal+",poolMaxIdle:"+poolMaxIdle+",poolMaxWait:"+poolMaxWait+",timeout:"+timeout+",password:"+password);
			JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
			jedisPoolConfig.setMaxIdle(poolMaxIdle);
			jedisPoolConfig.setMaxTotal(poolMaxTotal);
			jedisPoolConfig.setMaxWaitMillis(poolMaxWait);
			Set<String> sentinels = new HashSet<String>();
			for(String hostPort : hostPorts) {
				sentinels.add(hostPort); // 此处放置ip及端口为 sentinel,如果有多个sentinel 则逐一add即可
			}
			JedisSentinelPool jedisPool = new JedisSentinelPool(sentinelMasterName, sentinels, jedisPoolConfig, timeout, password, 0);
			return new JedisClient(null, jedisPool, null);
		}else {
			if(hostPorts.length <= 1) {//standalone
				log.info("JedisPool创建！hosts:"+hosts+",poolMaxTotal:"+poolMaxTotal+",poolMaxIdle:"+poolMaxIdle+",poolMaxWait:"+poolMaxWait+",timeout:"+timeout+",password:"+password);
				JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
				jedisPoolConfig.setMaxIdle(poolMaxIdle);
				jedisPoolConfig.setMaxTotal(poolMaxTotal);
				jedisPoolConfig.setMaxWaitMillis(poolMaxWait);
				String arr[] = hostPorts[0].split(":");
				JedisPool jedisPool = new JedisPool(jedisPoolConfig, arr[0], Integer.valueOf(arr[1]), timeout, password, 0);
				return new JedisClient(jedisPool, null, null);
			}else {//cluster
				log.info("JedisCluster创建！hosts:"+hosts+",poolMaxTotal:"+poolMaxTotal+",poolMaxIdle:"+poolMaxIdle+",poolMaxWait:"+poolMaxWait+",timeout:"+timeout+",password:"+password);
				JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
				jedisPoolConfig.setMaxIdle(poolMaxIdle);
				jedisPoolConfig.setMaxTotal(poolMaxTotal);
				jedisPoolConfig.setMaxWaitMillis(poolMaxWait);
				Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
				for(String hostPort : hostPorts) {
					String arr[] = hostPort.split(":");
					jedisClusterNodes.add(new HostAndPort(arr[0], Integer.parseInt(arr[1])));
				}
				JedisCluster jedisCluster = new JedisCluster(jedisClusterNodes, timeout,timeout,5,password,jedisPoolConfig);
				return new JedisClient(null, null, jedisCluster);
			}
		}
	}
}
