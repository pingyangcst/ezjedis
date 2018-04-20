package com.github.xjs.ezjedis.service;

import redis.clients.jedis.JedisCommands;

/**
 * @author 605162215@qq.com
 *
 * @date 2018年4月20日 下午3:56:01<br/>
 */
public class JedisCommandsHolder {
	
	private static ThreadLocal<JedisCommands> jedisHolder = new ThreadLocal<JedisCommands>();
	
	public static JedisCommands getJedisCommands() {
		return  jedisHolder.get();
	}
	
	public static void setJedisCommands(JedisCommands jedisCommands) {
		 jedisHolder.set(jedisCommands);
	}
	
}
