package com.zhucode.mao.mongo.generic;

import org.jongo.MongoCollection;

public interface GenericMAO {
	
	public MongoCollection getCollection(String doc);
}
