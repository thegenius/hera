package com.lvonce.hera.serializer;

import com.lvonce.hera.context.RpcMessage;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.Serializer;

public class RpcMessageSerializer extends Serializer<RpcMessage> {

	@Override
	public void write(Kryo kryo, Output output, RpcMessage object) {
		output.writeInt(object.getMessageType());
		kryo.writeClassAndObject(output, object.getContent());
	}

	@Override
	public RpcMessage read(Kryo kryo, Input input, Class<RpcMessage> type) {
		int messageType = input.readInt();
		Object content = (Object)kryo.readClassAndObject(input);
		return new RpcMessage(messageType, content);
	}
}
