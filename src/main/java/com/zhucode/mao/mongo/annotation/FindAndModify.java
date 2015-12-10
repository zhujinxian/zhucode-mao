package com.zhucode.mao.mongo.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FindAndModify {
	String coll() default "";
	String cnd() default "";
	String prj() default "";
	String sort() default "";
	String with() default "";
	boolean remove() default false;
	boolean upsert() default false;
	boolean isNew() default true;
}
