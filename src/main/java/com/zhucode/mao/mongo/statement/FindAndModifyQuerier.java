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
import com.zhucode.mao.mongo.annotation.FindAndModify;

public class FindAndModifyQuerier implements Querier {
	
	private Jongo jongo;
	
	public FindAndModifyQuerier(Jongo jongo) {
		this.jongo = jongo;
	}
	@Override
	public Object execute(StatementRuntime runtime) {
		
		Method m = runtime.getMethod();
		FindAndModify find = m.getAnnotation(FindAndModify.class);
		String doc = find.coll();
		String cnd = find.cnd();
		String prj = find.prj();
		String sort = find.sort();
		boolean upsert = find.upsert();
		boolean remove = find.remove();
		boolean isNew = find.isNew();
		String with = find.with();
		
		int queryParasNum = StringUtils.countMatches(cnd, "#");
		int projectParasNum = StringUtils.countMatches(prj, "#");
		int withParasNum = StringUtils.countMatches(with, "#");
		int sortParasNum = 0;
		
		if (sort.equals("#")) {
			sortParasNum = 1;
		}
		
		List<Object> paras = runtime.getParameters();
		if (paras.size() < queryParasNum + projectParasNum + sortParasNum) {
			return null;
		}
		
		List<Object> queryParas = paras.subList(0, queryParasNum);
		List<Object> projectParas = paras.subList(queryParasNum, queryParasNum + projectParasNum);
		List<Object> withParas = paras.subList(queryParasNum + projectParasNum, queryParasNum + projectParasNum + withParasNum);
		List<Object> sortObjs = paras.subList(queryParasNum + projectParasNum + withParasNum, paras.size());
		
		
		org.jongo.FindAndModify f = null;
		
		if (cnd.equals("")) {
			f = jongo.getCollection(doc).findAndModify();
		} else if (cnd.equals("#")){
			f = jongo.getCollection(doc).findAndModify(paras.get(0).toString());
		} else if (cnd.equals("#id")){
			f = jongo.getCollection(doc).findAndModify("{_id : #}",  paras.get(0));
		} else if (cnd.equals("#oid")){
			f = jongo.getCollection(doc).findAndModify("{_id:#}", new ObjectId(paras.get(0).toString()));
		} else if (queryParasNum > 0){
			f = jongo.getCollection(doc).findAndModify(cnd, queryParas.toArray());
		} else {
			f = jongo.getCollection(doc).findAndModify(cnd);
		}
		
		if (!sort.equals("")) {
			if (sortParasNum == 1) {
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
		
		if (!prj.equals("")) {
			if (projectParasNum > 0) {
				f = f.projection(prj, projectParas.toArray());
			} else {
				f = f.projection(prj);
			}
			
		}
		
		if (with.equals("") && remove) {
			f.remove();
		} else if (withParasNum > 0) {
			f.with(with, withParas.toArray());
		} else {
			f.with(with);
		}
		
		if (isNew) {
			f.returnNew();
		}
		
		if (upsert) {
			f.upsert();
		}
		
		Class<?> returnType = m.getReturnType();
		return f.as(returnType);
	}
	
}
