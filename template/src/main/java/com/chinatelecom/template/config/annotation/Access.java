package com.chinatelecom.template.config.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface Access {

    /**
     * 是否校验登录
     *
     * @return
     */
    boolean login() default false;

    /**
     * 是否校验权限
     *
     * @return
     */
    boolean privilege() default false;
}
