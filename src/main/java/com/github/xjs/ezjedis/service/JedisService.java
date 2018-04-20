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
	
	/**
	 * 只是在这里释放连接，可以配合AOP或者拦截器使用
	 * */
	public void releaseConnection() {
		jedisClient.releaseConnection();
	}
	
	public boolean exists(final KeyPrefix prefix,final String key) {
		return this.exists(prefix, key, false);
	}
	
	public boolean exists(final KeyPrefix prefix,final String key, final boolean releaseNow) {
		return jedisClient.exists(prefix, key, releaseNow);
	}
	
	public <T> boolean set(final KeyPrefix prefix, final String key, final T req) {
		return this.set(prefix, key, req, false, false);
	}
	
	public <T> boolean set(final boolean releaseNow, final KeyPrefix prefix, final String key, final T req) {
		return this.set(prefix, key, req, false, releaseNow);
	}
	
	public <T> boolean set(final KeyPrefix prefix, final String key, final T req, final boolean onlyNotExist) {
		return this.set(prefix, key, req, onlyNotExist, false);
	}
	
	public <T> boolean set(final KeyPrefix prefix, final String key, final T req, final boolean onlyNotExist, final boolean releaseNow) {
		return jedisClient.set(prefix, key, req, onlyNotExist, releaseNow);
	}
	
	public <T> T get(final KeyPrefix prefix, final String key, final Class<T> clazz) {
		return this.get(prefix, key, clazz, false);
	}
	
	public <T> T get(final KeyPrefix prefix, final String key, final Class<T> clazz, final boolean releaseNow) {
		return jedisClient.get(prefix, key, clazz, releaseNow);
	}
	
	public <T> List<T> getList(final KeyPrefix prefix, final String key, final Class<T> clazz) {
		return this.getList(prefix, key, clazz, false);
	}
	
	public <T> List<T> getList(final KeyPrefix prefix, final String key, final Class<T> clazz, final boolean releaseNow) {
		return jedisClient.getList(prefix, key, clazz, releaseNow);
	}
	
	public boolean delete(final KeyPrefix prefix) {
		return this.delete(prefix, false);
	}
	
	public boolean delete(final KeyPrefix prefix,  final boolean releaseNow) {
		return jedisClient.delete(prefix, releaseNow);
	}
	
	public boolean delete(final KeyPrefix prefix, final String key) {
		return this.delete(prefix, key, false);
	}
	
	public boolean delete(final KeyPrefix prefix, final String key, final boolean releaseNow ) {
		return jedisClient.delete(prefix, key, releaseNow);
	}
	
	public boolean delete(final KeyPrefix prefix, final String... keys) {
		return this.delete(false, prefix, keys);
	}
	
	public boolean delete(final boolean releaseNow, final KeyPrefix prefix, final String... keys) {
		if(keys == null || keys.length <= 0) {
			return false;
		}
		return this.delete(prefix, Arrays.asList(keys), releaseNow);
	}
	
	public boolean delete(final KeyPrefix prefix, final List<String> keys, final boolean releaseNow) {
		return jedisClient.delete(prefix, keys, releaseNow);
	}
	
	public boolean delete(final PrefixAndKey pk) {
		return this.delete(pk, false);
	}
	
	public boolean delete(final PrefixAndKey pk, final boolean releaseNow) {
		return jedisClient.delete(pk, releaseNow);
	}
	
	public boolean delete(final List<PrefixAndKey> pks) {
		return this.delete(pks, false);
	}
	
	public boolean delete(final List<PrefixAndKey> pks, final boolean releaseNow) {
		return jedisClient.delete(pks, releaseNow);
	}
	
	public boolean deleteAll() {
		return this.deleteAll(false);
	}
	
	public boolean deleteAll(final boolean releaseNow) {
		return jedisClient.deleteAll(releaseNow);
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
