package com.lvonce.hera.context;

import io.netty.channel.Channel;

/**
 * wrap the RpcMessage and add a Channel field to keep the channel which this request is from
 */
public class RpcMessageContext {
	private final Channel channel;
	private final RpcMessage message;
	
	public RpcMessageContext(RpcMessage message, Channel channel) {
		this.message = message;
		this.channel = channel;
	}
	
	public RpcMessage getMessage() {
		return this.message;
	}
	
	public Channel getChannel() {
		return this.channel;
	}
}
