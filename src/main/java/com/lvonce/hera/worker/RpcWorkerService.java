package com.lvonce.hera.worker;

import com.lvonce.hera.future.RpcFuture;
import com.lvonce.hera.context.RpcMessageContext;

public interface RpcWorkerService {
	public void start(int workerNum);
	public void accept(RpcMessageContext context);
	public void register(int id, RpcFuture rpcFuture);
	public<T> void export(Class<T> serviceInterface, T serviceProvider);
	public<T> void export(Class<T> serviceInterface, T serviceProvider, String serviceAlias);
}
