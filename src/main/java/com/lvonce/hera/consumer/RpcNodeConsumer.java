package com.lvonce.hera.consumer;

import com.lvonce.hera.future.RpcFuture;
import com.lvonce.hera.netty.NettyRpcNode;
import io.netty.channel.Channel;

public class RpcNodeConsumer {

	private String serviceName; 
	private String host;
	private int port;

	private int timeoutMills;
	private Channel channel;

	public RpcNodeConsumer(String serviceName, String host, int port, int timeoutMills) {
		this.serviceName = serviceName;
		this.host = host;
		this.port = port;	
		this.timeoutMills = timeoutMills;
		this.channel = null;
	}

	public Object call(String methodName, String sigName, Object ... args) {
		this.channel = NettyRpcNode.getChannel(host, port, this.channel);	
		return NettyRpcNode.call(this.channel, this.serviceName, methodName, sigName, args); 
	}

	public Object invoke(String methodName, String sigName, Object ... args) {
		this.channel = NettyRpcNode.getChannel(host, port, this.channel);	
		RpcFuture rpcFuture = NettyRpcNode.call(this.channel, this.serviceName, methodName, sigName, args);
		if (timeoutMills <= 0) {
			return rpcFuture.get();
		} else {
			return rpcFuture.get(this.timeoutMills);
		}
	}
}

