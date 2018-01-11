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

### 读写Key

### 读取key

```java
public <T> T get(final KeyPrefix prefix, final String key, final Class<T> clazz) {
  return jedisClient.get(prefix, key, clazz);
}
	
public <T> List<T> getList(final KeyPrefix prefix, final String key, final Class<T> clazz) {
  return jedisClient.getList(prefix, key, clazz);
}
```
### 写入key

```java
public <T> boolean set(final KeyPrefix prefix, final String key, final T req, final boolean onlyNotExist) {
  return jedisClient.set(prefix, key, req, onlyNotExist);
}
```

### 删除Key
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

### 分布式锁
```java
public boolean lock(final KeyPrefix prefix,final String key, final int waitSeconds) {
  return jedisClient.lock(prefix, key, waitSeconds);
}
```
