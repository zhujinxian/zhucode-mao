package com.zhucode.mao.mongo.context.spring;

import java.lang.reflect.Proxy;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import com.zhucode.mao.mongo.annotation.MAO;
import com.zhucode.mao.mongo.context.MongoInvocationHandler;
import com.zhucode.mao.mongo.dataaccess.DataSourceFactory;

public class MongoFactoryBean implements FactoryBean, InitializingBean {

	protected Class<?> objectType;

	protected Object maoObject;
	
	protected DataSourceFactory dataSourceFactory;;

	public void afterPropertiesSet() throws Exception {

	}

	@Override
	public Object getObject() throws Exception {
		if (maoObject == null) {
			maoObject = createMAO();
			Assert.notNull(maoObject);
		}
		return maoObject;
	}

	public void setObjectType(Class<?> objectType) {
		this.objectType = objectType;
	}
	
	private Object createMAO() {
		try {
			MongoInvocationHandler handler = new MongoInvocationHandler(dataSourceFactory, objectType);
			return Proxy.newProxyInstance(ClassUtils.getDefaultClassLoader(),
					new Class[] { objectType }, handler);
		} catch (RuntimeException e) {
			throw new IllegalStateException("failed to create bean for "
					+ this.objectType.getName(), e);
		}
	}

	@Override
	public Class<?> getObjectType() {
		return objectType;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public DataSourceFactory getDataSourceFactory() {
		return dataSourceFactory;
	}

	public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
		this.dataSourceFactory = dataSourceFactory;
	}

	
	
}
