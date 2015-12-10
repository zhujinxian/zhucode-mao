package com.zhucode.mao.mongo.dataaccess;

import java.util.HashMap;
import java.util.Map;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;

/**
 * @author zhu jinxian
 * @date  2015年12月4日
 * 
 */
public class DefaultDataSourceFactory implements DataSourceFactory {

	private MongoClient mongo;
	
	private Map<String, DB> dbs = new HashMap<String, DB>();
	
	public DefaultDataSourceFactory(MongoClientOptions mc, String host) {
		mongo = new MongoClient(host, mc);
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	public DB getDataSource(String db) {
		if (dbs.containsKey(db)) {
			return dbs.get(db);
		}
		synchronized (this) {
			DB d = mongo.getDB(db);
			dbs.put(db, d);
			return d;
		}
		
	}

}
