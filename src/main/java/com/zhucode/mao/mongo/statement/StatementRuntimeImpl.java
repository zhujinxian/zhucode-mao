
package com.zhucode.mao.mongo.statement;

import java.lang.reflect.Method;
import java.util.List;

import com.zhucode.mao.mongo.annotation.MAO;


public class StatementRuntimeImpl implements StatementRuntime {

	private MAO mao;
	private Method method;
	private List<Object> parameters;
	
	private final Class<?>[] genericReturnTypes; 

	public StatementRuntimeImpl(MAO mao, Method method, List<Object> parameters) {
		this.mao = mao;
		this.method = method;
		this.parameters = parameters;
		this.genericReturnTypes = GenericUtils.getActualClass(method.getGenericReturnType());
	}

	@Override
	public MAO getMao() {
		return mao;
	}

	@Override
	public Method getMethod() {
		return method;
	}

	@Override
	public List<Object> getParameters() {
		return parameters;
	}

	@Override
	public Class<?>[] getGenericReturnTypes() {
		return genericReturnTypes;
	}
}
