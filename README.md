# ezjedis
基于Jedis的简单易用的Redis客户端工具

## 特性
* 支持Cluster、Sentinel、Standalone配置
* 封装缓存Key
* 封装分布式锁

## 使用

### Standalone配置
> redis.hosts=10.110.3.62:6379

### Sentinel配置
>redis.hosts=10.110.3.62:6379,10.110.3.62:6479,10.110.3.62:6579

>redis.replication=true

### Cluster配置
>redis.hosts=10.110.3.62:6379,10.110.3.62:6479,10.110.3.62:6579

### 启用EzJedis ###
```java
@EnableEzJedis
@SpringBootApplication
public class MainApplication {
	    public static void main(String[] args) throws Exception {
	       	SpringApplication.run(MainApplication.class, args);
	    }
}
```

### 注入JedisService ###

```java
@Autowired
JedisService jedisSevice;
```

### 读取key ###

```java
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
```
### 写入key ###

```java
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
```

### 删除Key ###
```java
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
```

### 分布式锁 ###
```java
public String lock(final KeyPrefix prefix,final String key, final int waitSeconds) {
  return jedisClient.lock(prefix, key, waitSeconds);
}
public boolean unLock(final KeyPrefix prefix,final String key, final String oldValue) {
  return jedisClient.unLock(prefix, key, oldValue);
}
```

--------------------

### 连接释放管理 ###
	1、可以在Controller执行期间使用同一个连接
	2、可以在自己的项目中添加EzjedisInterceptor这个拦截器，用于在Controller方法执行末尾释放redis连接。
	3、可以在Controller的任意地方手动结束掉连接，只需要调用JedisService.releaseConnection()即可。
	4、可以调用带有releaseNow参数的方法，方法结束后会关闭掉连接
----------------
