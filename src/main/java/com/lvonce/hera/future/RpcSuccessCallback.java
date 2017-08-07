package com.lvonce.hera.future;

import com.lvonce.hera.context.RpcRequest;
import com.lvonce.hera.context.RpcResponse;
import com.lvonce.hera.exception.RpcException;

@FunctionalInterface
public interface RpcSuccessCallback {
	public void apply(RpcRequest request, Object result);
}
