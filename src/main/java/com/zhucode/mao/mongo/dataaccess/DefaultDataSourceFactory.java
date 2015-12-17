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

	private Map<String, String> dbmap;
	
	public DefaultDataSourceFactory(MongoClientOptions mc, String host, Map<String, String> dbmap) {
		mongo = new MongoClient(host, mc);
		this.dbmap = dbmap;
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	public DB getDataSource(String db) {
		if (db.startsWith("$")) {
			String[] strs = db.replace("$", "").replace("{", "").replace("}", "").split(":");
			String dbKey = strs[0].replaceAll(" ", "");
			String dbVal = null;
			if (strs.length == 2) {
				dbVal = strs[1].replaceAll(" ", "");
			}
			if (dbmap != null && dbmap.containsKey(dbKey)) {
				dbVal = dbmap.get(dbKey);
			}
			if (dbVal != null) {
				db = dbVal;
			}
		}
		if (dbs.containsKey(db)) {
			return dbs.get(db);
		}
		synchronized (this) {
			DB d = mongo.getDB(db);
			dbs.put(db, d);
			String hp = mongo.getAddress().getHost() 
					+ ":" + mongo.getAddress().getPort();
			System.out.println("connect to mongo [" + hp + "][" + db + "]");
			return d;
		}
		
	}

}
