
package com.zhucode.mao.mongo.statement;

import java.lang.reflect.Method;
import java.util.List;

import com.zhucode.mao.mongo.annotation.MAO;


public interface StatementRuntime {
	
	MAO getMao();
	
	Method getMethod();
	
	List<Object> getParameters();
	
    Class<?>[] getGenericReturnTypes();
   
}
