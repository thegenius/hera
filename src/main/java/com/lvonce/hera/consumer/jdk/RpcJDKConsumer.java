package com.lvonce.hera.consumer.jdk;

import com.lvonce.hera.logger.RpcLogger;
import com.lvonce.hera.future.RpcFuture;
import com.lvonce.hera.consumer.RpcNodeConsumer;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;

public class RpcJDKConsumer implements InvocationHandler {

	private final RpcNodeConsumer consumer;

	public RpcJDKConsumer(String scope, String host, int port, int timeoutMills) {
		this.consumer = new RpcNodeConsumer(scope, host, port, timeoutMills);
	}
	
	public RpcFuture call(String methodName, String sigName, Object ... args) {
		return (RpcFuture)this.consumer.call(methodName, sigName, args); 
	}
	
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {	
		String methodName = method.getName();
		return this.consumer.invoke(methodName, "jdk_sig", args);
	}
}
