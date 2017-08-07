package com.lvonce.hera.consumer.jdk;

import com.lvonce.hera.logger.RpcLogger;
import com.lvonce.hera.future.RpcFuture;
import com.lvonce.hera.netty.NettyRpcNode;
import com.lvonce.hera.exception.RpcDisconnectedException;

import io.netty.channel.Channel;
import java.lang.reflect.Proxy;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;


public class RpcJDKConsumerOld implements InvocationHandler {

	private long timeoutMills = 0;
	private String scope;
	private String host;
	private int port;

	private NettyRpcNode channelFactory;	
	private Channel channel; // channel cached for performance

	public RpcJDKConsumerOld(String scope, String host, int port, long timeoutMills) {
		this.host = host;
		this.port = port;	
		this.scope = scope;
		this.timeoutMills = timeoutMills;
	}
	
	public RpcFuture call(String methodName, Object ... args) {
		this.channel = NettyRpcNode.getChannel(host, port, this.channel);	
		return NettyRpcNode.call(this.channel, scope, methodName, "jdk_sig", args); 
	}
	
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {	

		String methodName = method.getName();
		RpcFuture rpcFuture = call(methodName, args);
		if (rpcFuture == null) {
			RpcLogger.info(RpcJDKConsumer.class, "RpcJDKConsumer is unavailable when disconnect with the server.");
			return null;
		}
		
		Object result;
		if (timeoutMills == 0) {
			result = rpcFuture.get();
		} else {
			result = rpcFuture.get(timeoutMills);
		}
		return result;
	}
}
