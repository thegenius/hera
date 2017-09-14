package com.lvonce.hera;

import com.lvonce.hera.netty.NettyRpcNode;
import com.lvonce.hera.consumer.RpcConsumerFactory;

public class HeraNode {
	
	public static<T> void exports(int port, T provider, Class<T> service, Class ... serviceAlias) {
	 	NettyRpcNode.export(port, provider, service, serviceAlias);	
	}

	public static void start(int port) {
		NettyRpcNode.start(port);
	}

	public static void run() {
	 	NettyRpcNode.serve();	
	 	NettyRpcNode.sync();	
	}

	public static void serve() {
	 	NettyRpcNode.serve();	
	}
	
	public static void sync() {
	 	NettyRpcNode.sync();	
	}

	public static<T> T imports(Class<T> service, String host, int port, int timeoutMills) {
		return RpcConsumerFactory.create(
			RpcConsumerFactory.Type.ASM_PROXY, 
			service, 
			timeoutMills, 
			host, 
			port);
	}
	
	public static<T> T requires(Class<T> service, String host, int port, int timeoutMills) {
		return RpcConsumerFactory.create(
			RpcConsumerFactory.Type.ASM_ASYNC_PROXY, 
			service, 
			timeoutMills, 
			host, 
			port);
	}


	public static<T> T imports(Class<T> service, String host, int port) {
		return RpcConsumerFactory.create(
			RpcConsumerFactory.Type.ASM_PROXY, 
			service, 
			0, 
			host, 
			port);
	}
}
