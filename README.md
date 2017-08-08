This is a brave new java rpc framework.

Support:  
	[1] asm generated proxy, faster than jdk dynamic proxy.  
	[2] function overload without annotations and configures.    
	[3] future based async call.  
	[3] server and client in the same node with same thread pool.    
    [4] clean exception message, when you have trouble then you will have useful tip.	

- Hello World
```
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
			return "Hello " + name;
		}
	}

	public static void main(String[] args) {
		if (args[0].equals("a")) {
			NettyRpcNode.export(Service.class, new Provider());
			NettyRpcNode.start(3721);
		}

		if (args[0].equals("b")) {
			Service service = RpcConsumerFactory.create(RpcConsumerFactory.Type.ASM_PROXY, Service.class, 2000, "127.0.0.1", 3721);
			String result = service.hello("World!");
			RpcLogger.info(App.class, result);
		}
	}
}
```
