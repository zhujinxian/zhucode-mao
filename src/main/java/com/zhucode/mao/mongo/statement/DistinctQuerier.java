package com.zhucode.mao.mongo.statement;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import com.zhucode.mao.mongo.annotation.Distinct;

public class DistinctQuerier implements Querier {

	private Jongo jongo;
	
	public DistinctQuerier(Jongo jongo) {
		this.jongo = jongo;
	}
	
	
	@Override
	public Object execute(StatementRuntime runtime) {
		Method m = runtime.getMethod();
		Distinct distinct = m.getAnnotation(Distinct.class);
		String doc = distinct.coll();
		String cnd = distinct.cnd();
		String key = distinct.key();
		
		int queryParasNum = StringUtils.countMatches(cnd, "#");
		
		List<Object> paras = runtime.getParameters();
		if (paras.size() < queryParasNum) {
			return null;
		}
		
		List<Object> queryParas = paras.subList(0, queryParasNum);
		
		MongoCollection mc = this.jongo.getCollection(doc);
		org.jongo.Distinct d = mc.distinct(key);
		
		if (cnd.equals("")) {
			d = d.query("");
		} else if (queryParasNum > 0){
			d = d.query(cnd, queryParas.toArray());
		} else {
			d = d.query(cnd);
		}
		
		Class<?> returnType = m.getReturnType();
		
		if (returnType.isArray()) {
			Class<?> type = returnType.getComponentType();
			List<?> ret = d.as(type);
			Object array = Array.newInstance(type, ret.size());
			int i = 0;
			Iterator<?> it = ret.iterator();
			while (it.hasNext()) {
				Array.set(array, i++, it.next());
			}
			return array;	
		} else if (returnType == List.class) {
			Class<?> type = runtime.getGenericReturnTypes()[0];
			List<?> ret = d.as(type);
			return ret;	
		} 
		return null;
	}

}
