package com.zhucode.mao.mongo.generic;

import org.jongo.Jongo;
import org.jongo.MongoCollection;

public class GenericMAOImp implements GenericMAO {
	
	private Jongo jongo;
	
	public GenericMAOImp(Jongo jongo) {
		this.jongo = jongo;
	}

	@Override
	public MongoCollection getCollection(String doc) {
		return this.jongo.getCollection(doc);
	}
}
