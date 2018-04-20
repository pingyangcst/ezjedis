package com.github.xjs.ezjedis.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.github.xjs.ezjedis.service.JedisService;

/**
 * @author 605162215@qq.com
 *
 * @date 2018年4月20日 下午4:50:47<br/>
 */
@Service
public class EzjedisInterceptor extends HandlerInterceptorAdapter{
	
	@Autowired
	JedisService jedisService;
	
	@Override
	public void afterCompletion(
			HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		jedisService.releaseConnection();
	}
	
}
