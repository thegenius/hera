package com.lvonce.hera.provider;

import com.lvonce.hera.logger.RpcLogger;
import com.lvonce.hera.context.RpcRequest;
import com.lvonce.hera.context.RpcResponse;
import com.lvonce.hera.context.RpcMessage;
import com.lvonce.hera.context.RpcMessageContext;
import com.lvonce.hera.exception.RpcException;
import com.lvonce.hera.exception.RpcExecuteException;
import com.lvonce.hera.exception.RpcMethodArgsNotMatchException;

import java.lang.InterruptedException;
import java.lang.IllegalArgumentException;

import io.netty.channel.Channel;
import com.esotericsoftware.reflectasm.MethodAccess;

public class ReflectasmProvider implements Provider {
	private final MethodAccess methodAccess;
	private final Object serviceProvider;

	public<T> ReflectasmProvider(Class<T> serviceInterface, T serviceProvider) {
		this.methodAccess = MethodAccess.get(serviceInterface);
		this.serviceProvider = serviceProvider;
	}

	@Override
	public Object call(String methodName, String signature, Object[] args) {
		try {
			int methodIndex = this.methodAccess.getIndex(methodName, args.length);	
		 	return this.methodAccess.invoke(this.serviceProvider, methodIndex, args);
		} catch (IllegalArgumentException e) {
			throw new RpcMethodArgsNotMatchException(methodName);
		}
	}

}

