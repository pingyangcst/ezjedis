package com.github.xjs.ezjedis.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.xjs.ezjedis.MainApplication;
import com.github.xjs.ezjedis.bean.User;
import com.github.xjs.ezjedis.key.UserKey;

/**
 * @author 605162215@qq.com
 *
 * @date 2018年1月11日 上午11:14:28<br/>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=MainApplication.class)
public class JedisServiceTest {
	
	@Autowired
	JedisService jedisSevice;
	
	@Before
	public void before() {
		jedisSevice.deleteAll();
	}
	
	@After
	public void after() {
		jedisSevice.releaseConnection();
	}
	
	@Test
	public void testExist() {
		boolean exists = jedisSevice.exists(UserKey.getByUserId, "100");
		Assert.assertFalse(exists);
	}
	
	@Test
	public void testSet() {
		boolean ret = jedisSevice.set(UserKey.getByUserId, "100", new User(1, "xjs"));
		Assert.assertTrue(ret);
	}
	
	@Test
	public void testSetNx() {
		boolean ret1 = jedisSevice.set(UserKey.getByUserIdExpire, "200", new User(2, "xjs"), false);
		Assert.assertTrue(ret1);
		boolean ret2 = jedisSevice.set(UserKey.getByUserIdExpire, "200", new User(2, "xjs"), false);
		Assert.assertTrue(ret2);
		boolean ret3 = jedisSevice.set(UserKey.getByUserIdExpire, "300", new User(2, "xjs"), true);
		Assert.assertTrue(ret3);
		boolean ret4 = jedisSevice.set(UserKey.getByUserIdExpire, "300", new User(2, "xjs"), true);
		Assert.assertFalse(ret4);
	}
	
	@Test
	public void testGet() {
		jedisSevice.set(UserKey.getByUserId, "100", new User(2, "xjs"));
		User user = jedisSevice.get(UserKey.getByUserId, "100",  User.class);
		Assert.assertNotNull(user);
		Assert.assertEquals(user.getId(), 2);
	}
	
	@Test
	public void testGetList() {
		List<User> users = new ArrayList<User>();
		users.add(new User(1, "xjs"));
		users.add(new User(2, "hello"));
		jedisSevice.set(UserKey.getByUserIds, "100", users);
		users = jedisSevice.getList(UserKey.getByUserIds, "100",  User.class);
		Assert.assertNotNull(users);
		Assert.assertEquals(users.size(), 2);
		Assert.assertEquals(users.get(0).getId(), 1);
	}
	
	@Test
	public void testDelete() {
		jedisSevice.set(UserKey.getByUserId, "100", new User(1, "xjs"));
		boolean ret = jedisSevice.delete(UserKey.getByUserId, "100");
		Assert.assertTrue(ret);
	}
	
	@Test
	public void testScanKeys() {
		jedisSevice.set(UserKey.getByUserId, "100", new User(1, "xjs"));
		jedisSevice.set(UserKey.getByUserId, "200", new User(2, "aaa"));
		List<String>keys = jedisSevice.scanKeys(UserKey.getByUserId);
		Assert.assertTrue(keys.size()==2);
	}
	
	@Test
	public void testLock() {
		String oldVaue = jedisSevice.lock(UserKey.getByUserId, "1000",1);
		Assert.assertTrue(oldVaue != null);
		oldVaue = jedisSevice.lock(UserKey.getByUserId, "1000",1);
		Assert.assertTrue(oldVaue == null);
	}
	@Test
	public void testLock2() {
		String oldValue = jedisSevice.lock(UserKey.lock, "1",15);
		Assert.assertTrue(oldValue != null);
		long start = System.currentTimeMillis();
		oldValue = jedisSevice.lock(UserKey.lock, "1",15);
		long end = System.currentTimeMillis();
		System.out.println("use time:"+(end-start));
		Assert.assertTrue(oldValue != null);
	}
	@Test
	public void testLock3() {
		String oldValue = jedisSevice.lock(UserKey.lock, "2",15);
		Assert.assertTrue(oldValue != null);
		jedisSevice.unLock(UserKey.lock, "2", oldValue);
		long start = System.currentTimeMillis();
		oldValue = jedisSevice.lock(UserKey.lock, "1",15);
		long end = System.currentTimeMillis();
		System.out.println("use time:"+(end-start));
		Assert.assertTrue(oldValue != null);
	}
	
	@Test
	public void testLock4() {
		Thread t1 = new Thread(new Runnable() {
			public void run() {
				String oldValue = jedisSevice.lock(UserKey.lock, "3",15);
				try {
					Thread.sleep(15);
				}catch(Exception e) {
					boolean ret = jedisSevice.unLock(UserKey.lock, "3", oldValue);
					Assert.assertFalse(ret);
				}
			}
		});
		Thread t2 = new Thread(new Runnable() {
			public void run() {
				String oldValue = jedisSevice.lock(UserKey.lock, "3",15);
				try {
					Thread.sleep(3000);
				}catch(Exception e) {
					boolean ret =	jedisSevice.unLock(UserKey.lock, "3", oldValue);
					Assert.assertTrue(ret);
				}
			}
		});
		t1.start();
		try {
			Thread.sleep(10);
		}catch(Exception e) {}
		t2.start();
	}
}
