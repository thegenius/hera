package com.lvonce.hera.provider;

import com.lvonce.hera.logger.RpcLogger;
import com.lvonce.hera.asm.RouterGenerator;
import com.lvonce.hera.exception.RpcException;


public class AsmProvider implements Provider {
	private final Object serviceProvider;
	private final Provider dispatcher;

	public<T> AsmProvider(Class<T> serviceInterface, T serviceProvider) {
		this.serviceProvider = serviceProvider;
		this.dispatcher = RouterGenerator.getForkRouter(Provider.class, serviceProvider);
	}

	@Override
	public Object call(String methodName, String signature, Object[] args) {
		return this.dispatcher.call(methodName, signature, args);
	}

}

