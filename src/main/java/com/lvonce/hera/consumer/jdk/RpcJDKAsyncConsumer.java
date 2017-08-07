package com.lvonce.hera.consumer.jdk;

import java.util.Set;
import java.util.HashSet;
import java.lang.reflect.Method;

import com.lvonce.hera.future.RpcFuture;
import com.lvonce.hera.logger.RpcLogger;
import com.lvonce.hera.exception.RpcMethodNotFoundException;

public class RpcJDKAsyncConsumer {
	private RpcJDKConsumer rpcClient;
	private Set<String> serviceMethodSet = new HashSet<String>();
	
	public RpcJDKAsyncConsumer(RpcJDKConsumer rpcClient, Class<?> interfaceClass) {
		this.rpcClient = rpcClient;
		Method[] methods = interfaceClass.getMethods();
		for (Method method : methods) {
			serviceMethodSet.add(method.getName());
		}
	}
	
	public RpcFuture call(String methodName, Object ... args) {
		if (!serviceMethodSet.contains(methodName)) {
			throw new RpcMethodNotFoundException(methodName);
		}

		RpcFuture callResult = rpcClient.call(methodName, "jdk_sig", args);
		
		if (callResult != null) {
			return callResult;
		} else {
			RpcLogger.info(RpcJDKAsyncConsumer.class, "RpcConsumer is unavailable when disconnect with the server.");
			return null;
		}
	}
}
