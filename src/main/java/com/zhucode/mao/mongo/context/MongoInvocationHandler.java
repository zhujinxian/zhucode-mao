package com.zhucode.mao.mongo.context;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.jongo.Jongo;
import org.jongo.Mapper;
import org.jongo.marshall.jackson.JacksonMapper;
import org.jongo.marshall.jackson.configuration.MapperModifier;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.mongodb.DB;
import com.zhucode.mao.mongo.annotation.Count;
import com.zhucode.mao.mongo.annotation.Delete;
import com.zhucode.mao.mongo.annotation.Distinct;
import com.zhucode.mao.mongo.annotation.Find;
import com.zhucode.mao.mongo.annotation.Insert;
import com.zhucode.mao.mongo.annotation.MAO;
import com.zhucode.mao.mongo.annotation.Update;
import com.zhucode.mao.mongo.dataaccess.DataSourceFactory;
import com.zhucode.mao.mongo.generic.GenericMAO;
import com.zhucode.mao.mongo.generic.GenericMAOImp;
import com.zhucode.mao.mongo.statement.CountQuerier;
import com.zhucode.mao.mongo.statement.DeleteQuerier;
import com.zhucode.mao.mongo.statement.DistinctQuerier;
import com.zhucode.mao.mongo.statement.FindQuerier;
import com.zhucode.mao.mongo.statement.InsertQuerier;
import com.zhucode.mao.mongo.statement.JongoStatement;
import com.zhucode.mao.mongo.statement.Querier;
import com.zhucode.mao.mongo.statement.Statement;
import com.zhucode.mao.mongo.statement.UpdateQuerier;


public class MongoInvocationHandler implements InvocationHandler {
	
	private DataSourceFactory dsf;
	
	private Class<?> objectType;
	
	private final ConcurrentHashMap<Method, Statement> statements = new ConcurrentHashMap<Method, Statement>();

	
	private Jongo jongo;
	
	private GenericMAO gmao;
	
	public MongoInvocationHandler(DataSourceFactory dsf, Class<?> objectType) {
		this.dsf = dsf;
		this.objectType = objectType;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
	
        // 调用object的方法
        if (method.getDeclaringClass() == Object.class) {
            return invokeObjectMethod(proxy, method, args);
        }
        if (method.getDeclaringClass() == GenericMAO.class) {
            return invokeGenericMAOtMethod(proxy, method, args);
        }
        Statement statement = getStatement(this.objectType.getAnnotation(MAO.class), method);
        
        List<Object> parameters = new ArrayList<>(0);
        if (args != null) {
        	parameters = Arrays.asList(args);
        }
        
		return statement.execute(parameters);
	}

	
	private Object invokeGenericMAOtMethod(Object proxy, Method method,Object[] args) {
		
		if (this.gmao == null) {
			MAO mao = this.objectType.getAnnotation(MAO.class);
			Jongo jongo = getJongo(mao.db(), mao.camel());
			this.gmao = new GenericMAOImp(jongo);
			
		}
		
		if (method.getName().equals("getCollection")) {
			return this.gmao.getCollection(args[0].toString());
		}
		
		throw new UnsupportedOperationException(this.getClass().getName() + "#" + method.getName());
	}

	private Object invokeObjectMethod(Object proxy, Method method, Object[] args)
			throws CloneNotSupportedException {
		String methodName = method.getName();
		if (methodName.equals("toString")) {
			return MongoInvocationHandler.this.toString();
		}
		if (methodName.equals("hashCode")) {
			return this.hashCode();
		}
		if (methodName.equals("equals")) {
			return args[0] == proxy;
		}
		if (methodName.equals("clone")) {
			throw new CloneNotSupportedException(
					"clone is not supported for jade mao.");
		}
		throw new UnsupportedOperationException(this.getClass().getName() + "#" + method.getName());

	}


	private Statement getStatement(MAO mao, Method method) {
		Statement statement = statements.get(method);
        if (statement == null) {
            synchronized (method) {
            
            	Jongo jongo = getJongo(mao.db(), mao.camel());
				
				Querier query = null;
            	if (method.isAnnotationPresent(Find.class)) {
            		query = new FindQuerier(jongo);
            	} else if (method.isAnnotationPresent(Insert.class)) {
            		query = new InsertQuerier(jongo);
            	} else if (method.isAnnotationPresent(Update.class)) {
            		query = new UpdateQuerier(jongo);
            	} else if (method.isAnnotationPresent(Delete.class)) {
            		query = new DeleteQuerier(jongo);
            	} else if (method.isAnnotationPresent(Count.class)) {
            		query = new CountQuerier(jongo);
            	} else if (method.isAnnotationPresent(Distinct.class)) {
            		query = new DistinctQuerier(jongo);
            	}
            	
            	if (query != null) {
            		statement = new JongoStatement(mao, method, query);
            		statements.put(method, statement);
            	}
            }
        }
		return statement;
	}
	
	
	private synchronized Jongo getJongo(String dbName, boolean camel) {
		if (this.jongo == null) {
			DB db = dsf.getDataSource(dbName);
			if (camel) {
				this.jongo = new Jongo(db, new JacksonMapper.Builder()
				.registerModule(new JodaModule())
				.addModifier(new MapperModifier() {
					@Override
					public void modify(ObjectMapper mapper) {
						mapper.setPropertyNamingStrategy(
								PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
					}
				}).build());
			} else {
				this.jongo = new Jongo(db, new JacksonMapper.Builder()
				.registerModule(new JodaModule())
				.enable(MapperFeature.AUTO_DETECT_GETTERS)
				.build());
			}
		}
			
		return this.jongo;
	}
	
}
