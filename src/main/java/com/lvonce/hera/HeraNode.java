package com.lvonce.hera;

import com.lvonce.hera.netty.NettyRpcNode;
import com.lvonce.hera.consumer.RpcConsumerFactory;

public class HeraNode {
	
	public static<T> void exports(T provider, Class<T> service, Class ... serviceAlias) {
	 	NettyRpcNode.export(service, provider);	
	}

	public static void start(int port) {
		NettyRpcNode.start(port);
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
