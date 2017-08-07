package com.lvonce.hera.example; 

import com.lvonce.hera.logger.RpcLogger;
import com.lvonce.hera.netty.NettyRpcNode;
import com.lvonce.hera.consumer.RpcConsumerFactory;
import com.lvonce.hera.context.RpcRequest;
import com.lvonce.hera.future.RpcFuture;
import com.lvonce.hera.future.RpcFailedCallback;
import com.lvonce.hera.future.RpcSuccessCallback;

public class App {
	public static void main(String[] args) {
		System.out.println("Hello");
		if (args[0].equals("a")) {
			NettyRpcNode.export(Service.class, new ServiceImpl(), ServiceProxy.class.getName());
			NettyRpcNode.start(3721);
		}

		if (args[0].equals("b")) {
			//Service service = RpcConsumerFactory.create(RpcConsumerFactory.Type.ASM_PROXY, Service.class, 2000, "127.0.0.1", 3721);
			//Service service = RpcConsumerFactory.create(RpcConsumerFactory.Type.JDK_PROXY, Service.class, 2000, "127.0.0.1", 3721);
			
			long start;
			long end;
			Object result = null;

			RpcLogger.info(App.class, "warm up ...");
			//for (int i=0; i<20; ++i) {
			//	result = service.hello("wang wei");
			//}

			//RpcLogger.info(App.class, "-------------- test[1] ---------------------");
			//start = System.nanoTime();
			//for (int i=0; i<100000; ++i) {
			//	result = service.hello("wang wei");
			//}
			//end = System.nanoTime();
			//RpcLogger.info(App.class, "result return new: "+ result);
			//System.out.println("time used: " + (end - start)+" ns");

			//RpcLogger.info(App.class, "-------------- test[2] ---------------------");
			//Service service2 = RpcConsumerFactory.create(RpcConsumerFactory.Type.ASM_PROXY, Service.class, 2000, "127.0.0.1", 3721);
		
			//start = System.nanoTime();
			//for (int i=0; i<100000; ++i) {
			//	result = service2.hello("wang wei");
			//}
			//end = System.nanoTime();
			//RpcLogger.info(App.class, "result return new: "+ result);
			//System.out.println("time used: " + (end - start)+" ns");

			ServiceProxy service3 = RpcConsumerFactory.create(RpcConsumerFactory.Type.ASM_ASYNC_PROXY, ServiceProxy.class, 2000, "127.0.0.1", 3721);
			RpcFuture future = service3.hello("wang wei");
			future.then((RpcRequest request, Object content)->{RpcLogger.info(App.class, content.toString());});
			future.get();
		}
	}
}
