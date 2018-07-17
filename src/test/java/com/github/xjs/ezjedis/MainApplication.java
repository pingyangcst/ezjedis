package com.github.xjs.ezjedis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.github.xjs.ezjedis.config.EnableEzJedis;

/**
 * @author 605162215@qq.com
 *
 * @date 2018年1月11日 下午2:05:02<br/>
 */
@EnableEzJedis
@SpringBootApplication
public class MainApplication {
	    public static void main(String[] args) throws Exception {
	       	SpringApplication.run(MainApplication.class, args);
	    }
}
