package com.lvonce.hera.handler;

import com.lvonce.hera.logger.RpcLogger;
import com.lvonce.hera.exception.RpcException;
import com.lvonce.hera.provider.ProviderManager;
import com.lvonce.hera.context.RpcMessageContext;
	
public class RpcRequestHandler implements RpcMessageHandler {
	private final ProviderManager providerManager;	
	public RpcRequestHandler(ProviderManager providerManager) {
		this.providerManager = providerManager;
	}

	@Override
	public void accept(RpcMessageContext context) {
		this.providerManager.call(context);
	}
}

