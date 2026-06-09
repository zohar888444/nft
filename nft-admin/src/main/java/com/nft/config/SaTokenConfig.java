package com.nft.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import cn.dev33.satoken.fun.SaParamFunction;
import cn.dev33.satoken.interceptor.SaRouteInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.router.SaRouterStaff;
import cn.dev33.satoken.stp.StpUtil;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new SaRouteInterceptor((req, res, handler) -> {
			SaRouter.match("/**", new SaParamFunction<SaRouterStaff>() {

				@Override
				public void run(SaRouterStaff r) {
					StpUtil.checkLogin();
					StpUtil.hasRole("admin");
				}
			});
		})).addPathPatterns("/**").excludePathPatterns("/login", "/error").excludePathPatterns("/css/**", "/images/**",
				"/js/**", "/plugins/**", "/audio/**", "/page/**", "/favicon.ico");
	}
}