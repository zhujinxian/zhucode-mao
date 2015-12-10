package com.zhucode.mao.mongo.statement;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import com.mongodb.WriteResult;
import com.zhucode.mao.mongo.annotation.Update;

public class UpdateQuerier implements Querier {

	private Jongo jongo;
	
	public UpdateQuerier(Jongo jongo) {
		this.jongo = jongo;
	}
	
	
	@Override
	public Object execute(StatementRuntime runtime) {
		Method m = runtime.getMethod();
		Update update = m.getAnnotation(Update.class);
		String doc = update.coll();
		String cnd = update.cnd();
		String with = update.with();
		boolean upsert = update.upsert();
		boolean multi = update.multi();
		
		int queryParasNum = StringUtils.countMatches(cnd, "#");
		int withParasNum = StringUtils.countMatches(with, "#");
		
		List<Object> paras = runtime.getParameters();
		if (paras.size() < queryParasNum + withParasNum) {
			return null;
		}
		
		List<Object> queryParas = paras.subList(0, queryParasNum);
		List<Object> withParas = paras.subList(queryParasNum, queryParasNum + withParasNum);
		
		MongoCollection mc = this.jongo.getCollection(doc);
		org.jongo.Update upder = null;
		if (cnd.equals("")) {
			upder = mc.update(cnd);
		} else if (cnd.equals("#oid")) {
			upder = mc.update(new ObjectId(paras.get(0).toString()));
		} else if (cnd.equals("#id")) {
			upder = mc.update("{_id : #}", paras.get(0));
		} else if (queryParasNum > 0){
			upder = mc.update(cnd, queryParas.toArray());
		} else {
			upder = mc.update(cnd);
		}
		
		if (upsert) {
			upder = upder.upsert();
		}
		
		if (multi) {
			upder = upder.multi();
		}
		WriteResult  wr = null;
		if (with.equals("")) {
			wr = upder.with("");
		} else if (withParasNum > 0){
			wr = upder.with(with, withParas.toArray());
		} else {
			wr = upder.with(with);
		} 
		
		return wr;
	}

}
