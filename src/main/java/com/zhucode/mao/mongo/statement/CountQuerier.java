package com.zhucode.mao.mongo.statement;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import com.zhucode.mao.mongo.annotation.Count;

public class CountQuerier implements Querier {

private Jongo jongo;
	
	public CountQuerier(Jongo jongo) {
		this.jongo = jongo;
	}
	
	@Override
	public Object execute(StatementRuntime runtime) {
		Method m = runtime.getMethod();
		Count count = m.getAnnotation(Count.class);
		String doc = count.doc();
		String cnd = count.cnd();
		
		int queryParasNum = StringUtils.countMatches(cnd, "#");
		
		List<Object> paras = runtime.getParameters();
		if (paras.size() < queryParasNum) {
			return null;
		}
		
		List<Object> queryParas = paras.subList(0, queryParasNum);
		
		MongoCollection mc = jongo.getCollection(doc);
		
		long num = 0;
		if (cnd.equals("")) {
			num = mc.count();
		} else if (queryParasNum > 0){
			num = mc.count(cnd, queryParas.toArray());
		} else {
			num = mc.count(cnd);
		}
		
		return num;
	}

}
