/**
 * 
 */
package com.github.xjs.ezjedis.service;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

/**
 * @author 605162215@qq.com
 *
 * @date 2017年9月11日 上午11:15:00
 */
@Configuration
public class JedisConfig {
	
	private static final Logger log = LoggerFactory.getLogger(JedisConfig.class);
	
	@Value("${redis.hosts}")
	private String hosts;
	
	@Value("${redis.timeout}")
	private int timeout;
	
	@Value("${redis.password}")
	private String password;
	
	@Value("${redis.poolMaxTotal}")
	private int poolMaxTotal;
	
	@Value("${redis.poolMaxIdle}")
	private int poolMaxIdle;
	
	@Value("${redis.poolMaxWait}")
	private int poolMaxWait;
	
	@Value("${redis.replication}")
	private boolean replication;
	
	@Bean
	public JedisClient JedisClientFactory() {
		String hostPorts[] = hosts.split(",");
		if(replication) {
			log.info("JedisSentinelPool创建！hosts:"+hosts+",poolMaxTotal:"+poolMaxTotal+",poolMaxIdle:"+poolMaxIdle+",poolMaxWait:"+poolMaxWait+",timeout:"+timeout+",password:"+password);
			JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
			jedisPoolConfig.setMaxIdle(poolMaxIdle);
			jedisPoolConfig.setMaxTotal(poolMaxTotal);
			jedisPoolConfig.setMaxWaitMillis(poolMaxWait);
			Set<String> sentinels = new HashSet<String>();
			for(String hostPort : hostPorts) {
				sentinels.add(hostPort); // 此处放置ip及端口为 sentinel,如果有多个sentinel 则逐一add即可
			}
			JedisSentinelPool jedisPool = new JedisSentinelPool("master7000", sentinels, jedisPoolConfig, timeout, password);
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
	
	public String getHosts() {
		return hosts;
	}
	public void setHosts(String hosts) {
		this.hosts = hosts;
	}
	public int getPoolMaxIdle() {
		return poolMaxIdle;
	}
	public void setPoolMaxIdle(int poolMaxIdle) {
		this.poolMaxIdle = poolMaxIdle;
	}
	public int getPoolMaxWait() {
		return poolMaxWait;
	}
	public void setPoolMaxWait(int poolMaxWait) {
		this.poolMaxWait = poolMaxWait;
	}

	public int getPoolMaxTotal() {
		return poolMaxTotal;
	}

	public void setPoolMaxTotal(int poolMaxTotal) {
		this.poolMaxTotal = poolMaxTotal;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isReplication() {
		return replication;
	}

	public void setReplication(boolean replication) {
		this.replication = replication;
	}
}
