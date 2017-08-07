package com.lvonce.hera.handler;
import com.lvonce.hera.context.RpcMessageContext;

public interface RpcMessageHandler {
	public void accept(RpcMessageContext context);
}
