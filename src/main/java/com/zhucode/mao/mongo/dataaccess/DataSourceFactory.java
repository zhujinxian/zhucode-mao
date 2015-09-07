package com.zhucode.mao.mongo.dataaccess;

import com.mongodb.DB;

public interface DataSourceFactory {
	DB getDataSource(String db);
}
