package com.github.xjs.ezjedis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author 605162215@qq.com
 *
 * @date 2018年7月17日 上午9:00:26<br/>
 */
@Component
public class EzJedisProperties {
	
	@Value("${redis.hosts}")
	private String hosts;
	
	@Value("${redis.timeout:3000}")
	private int timeout;
	
	@Value("${redis.password:}")
	private String password;
	
	@Value("${redis.poolMaxTotal:0}")
	private int poolMaxTotal;
	
	@Value("${redis.poolMaxIdle:0}")
	private int poolMaxIdle;
	
	@Value("${redis.poolMaxWait:0}")
	private int poolMaxWait;
	
	@Value("${redis.replication:false}")
	private boolean replication;
	
	@Value("${redis.masterName:}")
	private String masterName;

	public String getHosts() {
		return hosts;
	}

	public void setHosts(String hosts) {
		this.hosts = hosts;
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

	public int getPoolMaxTotal() {
		return poolMaxTotal;
	}

	public void setPoolMaxTotal(int poolMaxTotal) {
		this.poolMaxTotal = poolMaxTotal;
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

	public boolean isReplication() {
		return replication;
	}

	public void setReplication(boolean replication) {
		this.replication = replication;
	}

	public String getMasterName() {
		return masterName;
	}

	public void setMasterName(String masterName) {
		this.masterName = masterName;
	}
}
