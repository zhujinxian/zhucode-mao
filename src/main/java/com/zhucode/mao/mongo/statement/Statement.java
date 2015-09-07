
package com.zhucode.mao.mongo.statement;

import java.lang.reflect.Method;
import java.util.List;

import com.zhucode.mao.mongo.annotation.MAO;


public interface Statement {

    public MAO getMao();
    
    public Method getMethod();
    
    Object execute(List<Object> parameters);
}
