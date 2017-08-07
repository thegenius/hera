package com.lvonce.hera.future;

import com.lvonce.hera.context.RpcRequest;
import com.lvonce.hera.exception.RpcException;

@FunctionalInterface
public interface RpcFailedCallback {
	public void apply(RpcRequest request, RpcException exception);
}
