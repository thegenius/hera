package com.lvonce.hera.handler;

import com.lvonce.hera.future.RpcFuture;
import com.lvonce.hera.logger.RpcLogger;
import com.lvonce.hera.exception.RpcException;

import com.lvonce.hera.context.RpcMessage;
import com.lvonce.hera.context.RpcResponse;
import com.lvonce.hera.context.RpcMessageContext;

import java.util.concurrent.ConcurrentMap;

	
public class RpcResponseHandler implements RpcMessageHandler {
	private ConcurrentMap<Integer, RpcFuture> invokeIdRpcFutureMap;
	
	public RpcResponseHandler(ConcurrentMap<Integer, RpcFuture> invokeIdRpcFutureMap) {
		this.invokeIdRpcFutureMap = invokeIdRpcFutureMap;
	}

	@Override
	public void accept(RpcMessageContext context) {
		RpcMessage message = context.getMessage();
		RpcResponse response = (RpcResponse)message.getContent();	
		int id = response.getId();
		RpcFuture future = invokeIdRpcFutureMap.remove(id);
		future.accept(response);
	}
}

