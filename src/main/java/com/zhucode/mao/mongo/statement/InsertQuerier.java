package com.zhucode.mao.mongo.statement;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.jongo.Jongo;

import com.mongodb.WriteResult;
import com.zhucode.mao.mongo.annotation.Insert;

public class InsertQuerier implements Querier {

	private Jongo jongo;
	
	public InsertQuerier(Jongo jongo) {
		this.jongo = jongo;
	}
	
	@Override
	public Object execute(StatementRuntime runtime) {
		Method m = runtime.getMethod();
		Insert insert = m.getAnnotation(Insert.class);
		String doc = insert.coll();
		
		List<Object> paras = runtime.getParameters();
		
		List<Object> ps = new ArrayList<Object>();
		for (Object obj : paras) {
			if (obj instanceof List) {
				ps.addAll((List<?>) obj);
			} else if (obj.getClass().isArray()) {
				int len = Array.getLength(obj);
				for (int i = 0; i < len; i++) {
					ps.add(Array.get(obj, i));
				}
			} else {
				ps.add(obj);
			}
		}
		
		WriteResult wr = jongo.getCollection(doc).insert(ps.toArray());
		
		return wr;
	}

}
