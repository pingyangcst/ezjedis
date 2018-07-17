/**
 * 
 */
package com.test.ezjedis.key;

import com.github.xjs.ezjedis.key.AbstractKey;

/**
 * @author 605162215@qq.com
 *
 * @date 2017年9月11日 上午11:31:11
 */
public class UserKey extends AbstractKey{
	
	public UserKey(String value, int timeout) {
		super(value, timeout);
	}
	
	public static UserKey getByUserId = new UserKey("uid", 0);
	public static UserKey getByUserIdExpire = new UserKey("uide", 60);
	public static UserKey getByUserIds = new UserKey("uids", 60);
	public static UserKey lock = new UserKey("lock", 10);
}
