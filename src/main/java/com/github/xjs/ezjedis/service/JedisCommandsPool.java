package com.github.xjs.ezjedis.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;

/**
 * @author 605162215@qq.com
 *
 * @date 2018年4月20日 下午4:01:11<br/>
 */
public class JedisCommandsPool {
	
	private static Logger logger = LoggerFactory.getLogger(JedisCommandsPool.class);
	
	private JedisPool jp;
	
	private JedisSentinelPool js;
	
	private JedisCluster jc;

	public JedisCommandsPool(JedisPool jp,  JedisSentinelPool js,  JedisCluster jc) {
		this.jp= jp;
		this.js = js;
		this.jc = jc;
	}

	public JedisPool getJp() {
		return jp;
	}

	public void setJp(JedisPool jp) {
		this.jp = jp;
	}

	public JedisCluster getJc() {
		return jc;
	}

	public void setJc(JedisCluster jc) {
		this.jc = jc;
	}
	
	public JedisSentinelPool getJs() {
		return js;
	}

	public void setJs(JedisSentinelPool js) {
		this.js = js;
	}
	
	public void releaseJedisCommands() {
		JedisCommands jc = JedisCommandsHolder.getJedisCommands();
		if(jc != null) {
			logger.info("释放一个连接");
			JedisCommandsHolder.setJedisCommands(null);
			if(jc instanceof Jedis) {
				((Jedis)jc).close();
			}
		}
	}
	
	public JedisCommands getJedisCommands() {
		logger.info("获取一个连接");
		if(jc != null) {
			return jc;
		}else if(js != null){
			return js.getResource();
		}
		return jp.getResource();
	}
}
