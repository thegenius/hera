package com.lvonce.hera.context;

import io.netty.channel.Channel;
import com.lvonce.hera.serializer.SerializerType;

/**
 * wrap the RpcMessage
 * 		: add a channel field to keep the channel of the message
 *		: add a serializer type of the message
 */
public class RpcMessageContext {
	private RpcMessage message;
	private final Channel channel;
	private final SerializerType serializerType;
	
	public RpcMessageContext(RpcMessage message, Channel channel) {
		this.message = message;
		this.channel = channel;
		this.serializerType = SerializerType.getDefault();
	}
	
	public RpcMessageContext(RpcMessage message, Channel channel, SerializerType serializerType) {
		this.message = message;
		this.channel = channel;
		this.serializerType = serializerType;
	}

	@Override
	public String toString() {
		return "{serializerType: " + serializerType.toString() + ", message: " + message.toString() + "}";
	}

	public RpcMessage getMessage() {
		return this.message;
	}
	
	public void setMessage(RpcMessage message) {
		this.message = message;
	}

	public Channel getChannel() {
		return this.channel;
	}

	public SerializerType getSerializerType() {
		return this.serializerType;
	}
}
