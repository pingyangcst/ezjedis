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
public class DemoKey extends AbstractKey{
	
	public DemoKey(String value) {
		super(value);
	}
	
	public static DemoKey setKey = new DemoKey("sk");
}
