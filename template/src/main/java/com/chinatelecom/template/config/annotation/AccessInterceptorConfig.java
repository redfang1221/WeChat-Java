package com.chinatelecom.template.config.annotation;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Configuration
public class AccessInterceptorConfig implements WebMvcConfigurer {

    private static List<String> EXCLUDE_PATH = Arrays.asList("/resources/**","/views/**","/error","/login");

    @Resource
    private AccessInterceptor accessInterceptor;

    /**
     *   这个方法用来注册拦截器，我们自己写好的拦截器需要通过这里添加注册才能生效
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 自定义拦截器，添加拦截路径和排除拦截路径
        registry.addInterceptor(accessInterceptor).addPathPatterns("/**").excludePathPatterns(EXCLUDE_PATH);

    }
}
