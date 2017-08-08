package com.lvonce.hera.example; 

import com.lvonce.hera.logger.RpcLogger;
import com.lvonce.hera.future.RpcFuture;
import com.lvonce.hera.netty.NettyRpcNode;
import com.lvonce.hera.consumer.RpcConsumerFactory;

public class App {

	public static interface Service {
		public String hello(String name);
	}

	public static class Provider implements Service {
		public String hello(String name) {
			return "hello " + name;
		}
	}

	public static void main(String[] args) {
		if (args[0].equals("a")) {
			NettyRpcNode.export(Service.class, new Provider());
			NettyRpcNode.start(3721);
		}

		if (args[0].equals("b")) {
			Service service = RpcConsumerFactory.create(RpcConsumerFactory.Type.ASM_PROXY, Service.class, 2000, "127.0.0.1", 3721);
			Object result = service.hello("wang wei");
			RpcLogger.info(App.class, "result: " + result);
		}
	}
}
