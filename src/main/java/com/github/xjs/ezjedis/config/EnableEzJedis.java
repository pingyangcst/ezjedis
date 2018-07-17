package com.github.xjs.ezjedis.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * @author 605162215@qq.com
 *
 * @date 2018年7月17日 上午9:24:15<br/>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(EzJedisAutoConfiguration.class)
public @interface EnableEzJedis {

}
