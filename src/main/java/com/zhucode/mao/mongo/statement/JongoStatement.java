package com.zhucode.mao.mongo.statement;

import java.lang.reflect.Method;
import java.util.List;

import com.zhucode.mao.mongo.annotation.MAO;

public class JongoStatement implements Statement {
	
	
	private Querier query;
	private MAO mao;
	private Method method;
   
	

	public JongoStatement(MAO mao, Method m, Querier query) {
		this.query = query;
		this.mao = mao;
		this.method = m;
	}

	@Override
	public MAO getMao() {
		return this.mao;
	}

	@Override
	public Method getMethod() {
		return this.method;
	}

	@Override
	public Object execute(List<Object> parameters) {
		StatementRuntime sr = new StatementRuntimeImpl(mao, method, parameters);
		return this.query.execute(sr);
	}

}
