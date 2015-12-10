package com.zhucode.mao.mongo.statement;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import com.mongodb.WriteResult;
import com.zhucode.mao.mongo.annotation.Delete;

public class DeleteQuerier implements Querier {

	private Jongo jongo;
	
	public DeleteQuerier(Jongo jongo) {
		this.jongo = jongo;
	}
	
	@Override
	public Object execute(StatementRuntime runtime) {
		Method m = runtime.getMethod();
		Delete del = m.getAnnotation(Delete.class);
		String doc = del.coll();
		String cnd = del.cnd();
		
		int queryParasNum = StringUtils.countMatches(cnd, "#");
		
		List<Object> paras = runtime.getParameters();
		if (paras.size() < queryParasNum) {
			return null;
		}
		
		List<Object> queryParas = paras.subList(0, queryParasNum);
		
		MongoCollection mc = jongo.getCollection(doc);
		WriteResult  wr = null;
		if (cnd.equals("#oid")) {
			wr = mc.remove(new ObjectId(paras.get(0).toString()));
		} else if (cnd.equals("#id")) {
			wr = mc.remove("{_id : #}", paras.get(0));
		} else {
			if (queryParasNum > 0){
				wr = mc.remove(cnd, queryParas.toArray());
			} else {
				wr = mc.remove(cnd);
			}
		}
		
		return wr;
	}

}
