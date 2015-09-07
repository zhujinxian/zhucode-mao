package com.zhucode.mao.mongo.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Find {
	String cnd() default "";
	String doc() default "";
	String prj() default "";
	String sort() default "";
	int limit() default 0;
	int skip() default 0;
}
