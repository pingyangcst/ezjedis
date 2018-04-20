/**
 * 
 */
package com.github.xjs.ezjedis.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.github.xjs.ezjedis.key.KeyPrefix;
import com.github.xjs.ezjedis.key.PrefixAndKey;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

/**
 * @author 605162215@qq.com
 *
 * @date 2017年9月11日 下午3:55:54<br/>
 * 
 * Jedis客户端封装类，同时支持Cluster、Sentinel、Standalone模式
 */
public class JedisClient {
	
	JedisCommandsPool jcp;
	
	public JedisClient(JedisPool jp,JedisSentinelPool js, JedisCluster jc) {
		this.jcp = new JedisCommandsPool(jp, js, jc);
	}
	
	public void releaseConnection() {
		jcp.releaseJedisCommands();
	}
	
	public boolean exists(final KeyPrefix prefix,final String key, final boolean releaseNow) {
		JedisCommands jc = null;
		String realKey = prefix.getPrefix() + key;
		try {
			jc = getJedisCommands();
			return jc.exists(realKey);
		}finally {
			closeJedisCommands(releaseNow);
		}
	}
	
	public <T> boolean set(final KeyPrefix prefix, final String key, final T req, final boolean onlyNotExist, final boolean releaseNow) {
		if(req == null){
			return false;
		}
		String realKey = prefix.getPrefix() + key;
		String value = beanToString(req);
		JedisCommands jc = null;
		try {
			jc = getJedisCommands();
			int seconds = prefix.getExpireSeconds();
			String ret = null;
			if(seconds <= 0) {
				if(onlyNotExist) {//判断是否存在
					Long result = jc.setnx(realKey, value);
					ret = (result!=null&&result>0)?"OK":null;
				}else {
					ret = jc.set(realKey, value);
				}
			}else {
				if(onlyNotExist) {//判断是否存在
					ret = jc.set(realKey, value, "nx", "ex", seconds);
				}else {//不管是否存在
					ret = jc.setex(realKey,seconds ,value);
				}
			}
			return "OK".equals(ret);
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			closeJedisCommands(releaseNow);
		}
	}
	
	public <T> T get(final KeyPrefix prefix, final String key, final Class<T> clazz, final boolean releaseNow) {
		if(clazz == null){
			return null;
		}
		String realKey = prefix.getPrefix() + key;
		JedisCommands jc = null;
		try {
			jc = getJedisCommands();
			String value = jc.get(realKey);
			if(value == null || value.length() <= 0) {
				return null;
			}
			return stringToBean(value, clazz);
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			closeJedisCommands(releaseNow);
		}
	}
	
	public <T> List<T> getList(final KeyPrefix prefix, final String key, final Class<T> clazz, final boolean releaseNow) {
		if(clazz == null){
			return null;
		}
		String realKey = prefix.getPrefix() + key;
		JedisCommands jc = null;
		try {
			jc = getJedisCommands();
			String value = jc.get(realKey);
			if(value == null || value.length() <= 0) {
				return null;
			}
			return stringToBeanList(value, clazz);
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			closeJedisCommands(releaseNow);
		}
	}
	
	public boolean delete(final KeyPrefix prefix, final boolean releaseNow) {
		if(prefix == null) {
			return false;
		}
		List<String> realKeys = scanKeys(prefix);
		if(realKeys == null || realKeys.size() <= 0) {
			return false;
		}
		JedisCommands jc = null;
		try {
			jc = getJedisCommands();
			Long ret = 0L;
			if(jc instanceof Jedis) {
				ret = ((Jedis)jc).del(realKeys.toArray(new String[0]));
			}else {
				ret = ((JedisCluster)jc).del(realKeys.toArray(new String[0]));
			}
			return (ret != null && ret > 0);
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			closeJedisCommands(releaseNow);
		}
	}
	
	public boolean delete(final KeyPrefix prefix, final String key,final boolean releaseNow) {
		String realKey = prefix.getPrefix() + key;
		JedisCommands jc = null;
		try {
			jc = getJedisCommands();
			Long ret = jc.del(realKey);
			return (ret != null && ret > 0);
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			closeJedisCommands(releaseNow);
		}
	}
	
	public boolean deleteAll(final boolean releaseNow) {
		JedisCommands jc = null;
		try {
			jc = getJedisCommands();
			if(jc instanceof Jedis) {
				String ret = ((Jedis)jc).flushDB();
				return "OK".equals(ret);
			}else {
				return false;
			}
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			closeJedisCommands(releaseNow);
		}
	}
	
	public boolean delete(final PrefixAndKey pk, final boolean releaseNow) {
		if(pk == null) {
			return false;
		}
		KeyPrefix prefix = pk.getPrefix();
		String key = pk.getKey();
		return delete(prefix, key, releaseNow);
	}
	
	public boolean delete(final List<PrefixAndKey> pks, final boolean releaseNow) {
		if(pks == null || pks.size() <= 0) {
			return false;
		}
		String[] realKeys = new String[pks.size()];
		int i=0;
		for(PrefixAndKey pk : pks) {
			KeyPrefix prefix = pk.getPrefix();
			String key = pk.getKey();
			realKeys[i++] = prefix.getPrefix()+key;
		}
		JedisCommands jc = null;
		try {
			jc = getJedisCommands();
			if(jc instanceof JedisCluster) {
				((JedisCluster)jc).del(realKeys);
			}else {
				for(String realKey : realKeys) {
					jc.del(realKey);
				}
			}
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}finally {
			closeJedisCommands(releaseNow);
		}
	}
	
	public boolean delete(final KeyPrefix prefix, final List<String> keys, final boolean releaseNow) {
		String[] realKeys = new String[keys.size()];
		int i=0;
		for(String key : keys) {
			String realKey = prefix.getPrefix() + key;
			realKeys[i++] = realKey;
		}
		JedisCommands jc = null;
		try {
			jc = getJedisCommands();
			if(jc instanceof JedisCluster) {
				((JedisCluster)jc).del(realKeys);
			}else {
				for(String realKey : realKeys) {
					jc.del(realKey);
				}
			}
			return true;
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			closeJedisCommands(releaseNow);
		}
	}
	
	public String lock(KeyPrefix keyPrefix, String key, int waitSeconds) {
        int waitTimeout = waitSeconds * 1000;
        while (waitTimeout >= 0) {
            long expiresAt = System.currentTimeMillis() + keyPrefix.getExpireSeconds()*1000 + 1;
            String expiresAtStr = String.valueOf(expiresAt); //锁到期时间
            if (this.set(keyPrefix, key, expiresAtStr, true, false)) {//设置超时时间，防止客户端崩溃，锁得不到释放
                // lock acquired
                return expiresAtStr;
            }
            waitTimeout -= 100;
            try {
            	Thread.sleep(100);
            }catch(Exception e) {
            	e.printStackTrace();
            }
        }
        return null;
    }
	
	public boolean unLock(KeyPrefix prefix, String key, String oldValue) {
		if(prefix == null || key == null || oldValue == null) {
			return false;
		}
		//调用lua脚本
		 String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
		 String realKey = prefix.getPrefix() + key;
		 JedisCommands jc = null;
		 Object result = null;
		try {
			jc = getJedisCommands();
			if(jc instanceof Jedis) {//jedis
				result = ((Jedis)jc).eval(script, Collections.singletonList(realKey), Collections.singletonList(oldValue));
			}else {//集群
				result = ((JedisCluster)jc).eval(script, Collections.singletonList(realKey), Collections.singletonList(oldValue));
			}
			if(result!=null && result.toString().equals("1")) {
				return true;
			}
			return false;
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			closeJedisCommands(true);
		}
	}
	
	public List<String> scanKeys(KeyPrefix keyPrefix) {
		if(keyPrefix == null) {
			return null;
		}
		String prefix = keyPrefix.getPrefix();
		return scanKeys(prefix);
	}
	
	public List<String> scanKeys(String key) {
		JedisCommands jc = null;
		try {
			jc = getJedisCommands();
			List<String> keys = new ArrayList<String>();
			String cursor = "0";
			ScanParams sp = new ScanParams();
			sp.match("*"+key+"*");
			sp.count(100);
			do{
				ScanResult<String> ret = null;
				if(jc instanceof Jedis) {
					ret = ((Jedis)jc).scan(cursor, sp);
				}else {
					ret = ((JedisCluster)jc).scan(cursor, sp);
				}
				List<String> result = ret.getResult();
				if(result!=null && result.size() > 0){
					keys.addAll(result);
				}
				//再处理cursor
				cursor = ret.getStringCursor();
			}while(!cursor.equals("0"));
			return keys;
		} finally {
			closeJedisCommands(false);
		}
	}
	
	private JedisCommands getJedisCommands() {
		JedisCommands jc = JedisCommandsHolder.getJedisCommands();
		if(jc == null) {
			jc = jcp.getJedisCommands();
			JedisCommandsHolder.setJedisCommands(jc);
		}
		return jc;
	}

	private void closeJedisCommands(boolean releaseNow) {
		if(releaseNow) {
			jcp.releaseJedisCommands();
		}
	}
	
	private <T> String beanToString(T req) {
		Class<?> clazz = req.getClass();
		if(clazz == String.class || 
			clazz == int.class || clazz == Integer.class ||
			clazz == long.class || clazz == Long.class ||
			clazz == boolean.class || clazz == Boolean.class ||
			clazz == float.class || clazz == Float.class ||
			clazz == double.class || clazz == Double.class){
			return  req.toString();
		}else {
			return JSON.toJSONString(req);
		}
	}

	@SuppressWarnings({ "unchecked" })
	private <T> T stringToBean(String src, Class<T> clazz){
		if(clazz == String.class){
			return (T)src;
		}else if(clazz == int.class || clazz == Integer.class){
			return (T)Integer.valueOf(src);
		}else if(clazz == long.class || clazz == Long.class){
			return (T)Long.valueOf(src);
		}else if(clazz == boolean.class || clazz == Boolean.class){
			return (T)Boolean.valueOf(src);
		}else if(clazz == float.class || clazz == Float.class){
			return (T)Float.valueOf(src);
		}else if(clazz == double.class || clazz == Double.class){
			return (T)Double.valueOf(src);
		}else {
			return JSON.toJavaObject(JSON.parseObject(src), clazz);
		}
	}
	private <T> List<T> stringToBeanList(String src, Class<T> clazz){
		return JSON.parseArray(src, clazz);
	}

}
