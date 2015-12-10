package com.zhucode.mao.mongo.statement;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCursor;

import com.zhucode.mao.mongo.annotation.Find;

public class FindQuerier implements Querier {
	
	private Jongo jongo;
	
	public FindQuerier(Jongo jongo) {
		this.jongo = jongo;
	}
	@Override
	public Object execute(StatementRuntime runtime) {
		
		Method m = runtime.getMethod();
		Find find = m.getAnnotation(Find.class);
		String doc = find.coll();
		String cnd = find.cnd();
		String prj = find.prj();
		String sort = find.sort();
		int skip = find.skip();
		int limit = find.limit();
		
		int queryParasNum = StringUtils.countMatches(cnd, "#");
		int projectParasNum = StringUtils.countMatches(prj, "#");
		int sortParasNum = 0;
		
		if (sort.equals("#")) {
			sortParasNum = 1;
		}
		
		List<Object> paras = runtime.getParameters();
		if (paras.size() < queryParasNum + projectParasNum + sortParasNum) {
			return null;
		}
		
		List<Object> skipLimit = paras.subList(queryParasNum + projectParasNum + sortParasNum, paras.size());
		
		if (skipLimit.size() > 0) {
			skip = (int)skipLimit.get(0);
		}
		if (skipLimit.size() > 1) {
			limit = (int)skipLimit.get(1);
		}
		
		List<Object> queryParas = paras.subList(0, queryParasNum);
		List<Object> projectParas = paras.subList(queryParasNum, queryParasNum + projectParasNum);
		
		
		org.jongo.Find f = null;
		
		if (cnd.equals("")) {
			f = jongo.getCollection(doc).find();
		} else if (cnd.equals("#")){
			f = jongo.getCollection(doc).find(paras.get(0).toString());
		} else if (cnd.equals("#id")){
			Class<?> returnType = m.getReturnType();
			return jongo.getCollection(doc).findOne("{_id : #}",  paras.get(0)).as(returnType);
		} else if (cnd.equals("#oid")){
			Class<?> returnType = m.getReturnType();
			return jongo.getCollection(doc).findOne(new ObjectId(paras.get(0).toString())).as(returnType);
		} else if (queryParasNum > 0){
			f = jongo.getCollection(doc).find(cnd, queryParas.toArray());
		} else {
			f = jongo.getCollection(doc).find(cnd);
		}
		
		if (!sort.equals("")) {
			if (sortParasNum == 1) {
				List<Object> sortObjs = paras.subList(queryParasNum + projectParasNum, queryParasNum + projectParasNum + 1);
				String sortString = sortObjs.get(0).toString();
				if (!sortString.equals("") && !sortString.equals("{}")) {
					f = f.sort(sortString);
				}
			} else {
				String[] sorts = sort.split("#");
				for (String s : sorts) {
					f = f.sort(s);
				}
			}
		
		}
		
		if (skip > 0) {
			f = f.skip(skip);
		}
		
		if (limit > 0) {
			f = f.limit(limit);
		}
		
		if (!prj.equals("")) {
			if (projectParasNum > 0) {
				f = f.projection(prj, projectParas.toArray());
			} else {
				f = f.projection(prj);
			}
			
		}
		
		Class<?> returnType = m.getReturnType();
		
		if (returnType ==  MongoCursor.class) {
			Class<?> type = runtime.getGenericReturnTypes()[0];
			return f.as(type);
		} else if (returnType.isArray()) {
			Class<?> type = returnType.getComponentType();
			MongoCursor<?> mc = f.as(type);
			Object array = Array.newInstance(type, mc.count());
			int i = 0;
			while (mc.hasNext()) {
				Array.set(array, i++, mc.next());
			}
			return array;	
		} else if (returnType == List.class) {
			Class<?> type = runtime.getGenericReturnTypes()[0];
			MongoCursor<?> mc = f.as(type);
			List<Object> li = new ArrayList<Object>();
			while (mc.hasNext()) {
				li.add(mc.next());
			}
			return li;	
		} else {
			MongoCursor<?> mc = f.as(returnType);
			if (mc.hasNext()) {
				return mc.next();
			}
			return null;
		}
	}
	
}
