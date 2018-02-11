/**
 * 
 */
package com.github.xjs.ezjedis.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.xjs.ezjedis.key.KeyPrefix;
import com.github.xjs.ezjedis.key.PrefixAndKey;

/**
 * @author 605162215@qq.com
 *
 * @date 2017年9月11日 上午11:30:13<br/>
 * 
 * Jedis服务类，代理给{@link JedisClient}
 */

@Service
public class JedisService {

	@Autowired
	JedisClient jedisClient;
	
	public boolean exists(final KeyPrefix prefix,final String key) {
		return jedisClient.exists(prefix, key);
	}
	
	public <T> boolean set(final KeyPrefix prefix, final String key, final T req) {
		return set(prefix, key, req, false);
	}
	
	public <T> boolean set(final KeyPrefix prefix, final String key, final T req, final boolean onlyNotExist) {
		return jedisClient.set(prefix, key, req, onlyNotExist);
	}
	
	public <T> T get(final KeyPrefix prefix, final String key, final Class<T> clazz) {
		return jedisClient.get(prefix, key, clazz);
	}
	
	public <T> List<T> getList(final KeyPrefix prefix, final String key, final Class<T> clazz) {
		return jedisClient.getList(prefix, key, clazz);
	}
	
	public boolean delete(final KeyPrefix prefix) {
		return jedisClient.delete(prefix);
	}
	
	public boolean delete(final KeyPrefix prefix, final String key) {
		return jedisClient.delete(prefix, key);
	}
	
	public boolean delete(final KeyPrefix prefix, final String... keys) {
		if(keys == null || keys.length <= 0) {
			return false;
		}
		return jedisClient.delete(prefix, Arrays.asList(keys));
	}
	
	public boolean delete(final KeyPrefix prefix, final List<String> keys) {
		return jedisClient.delete(prefix, keys);
	}
	
	public boolean delete(final PrefixAndKey pk) {
		return jedisClient.delete(pk);
	}
	
	public boolean delete(final List<PrefixAndKey> pks) {
		return jedisClient.delete(pks);
	}
	
	public boolean deleteAll() {
		return jedisClient.deleteAll();
	}
	
	public List<String> scanKeys(final KeyPrefix prefix){
		return jedisClient.scanKeys(prefix);
	}
	
	public List<String> scanKeys(String key){
		return jedisClient.scanKeys(key);
	}
	
	public String lock(final KeyPrefix prefix,final String key, final int waitSeconds) {
		return jedisClient.lock(prefix, key, waitSeconds);
	}
	
	public boolean unLock(final KeyPrefix prefix,final String key, final String oldValue) {
		return jedisClient.unLock(prefix, key, oldValue);
	}
}
