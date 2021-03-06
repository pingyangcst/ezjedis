package com.test.ezjedis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.github.xjs.ezjedis.config.EnableEzJedis;

/**
 * @author 605162215@qq.com
 *
 * @date 2018年1月11日 下午2:05:02<br/>
 * 两种启用方式：
 * 1.在入口类添加@EnableEzJedis注解
 * 2.单独定义一个EzJedisConfig类，添加@EnableEzJedis注解
 */
@EnableEzJedis
@SpringBootApplication
public class MainApplication {
	    public static void main(String[] args) throws Exception {
	       	SpringApplication.run(MainApplication.class, args);
	    }
}
