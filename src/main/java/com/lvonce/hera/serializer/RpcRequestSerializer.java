package com.lvonce.hera.serializer;

import com.lvonce.hera.context.RpcRequest;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.Serializer;

public class RpcRequestSerializer extends Serializer<RpcRequest> {
	@Override
	public void write(Kryo kryo, Output output, RpcRequest object) {
		output.writeInt(object.getId());
		output.writeByte(object.getScopeName().length());
		output.write(object.getScopeName().getBytes());
		output.writeByte(object.getMethodName().length());
		output.write(object.getMethodName().getBytes());
		output.writeByte(object.getSigName().length());
		output.write(object.getSigName().getBytes());
		kryo.writeClassAndObject(output, object.getArgs());
	}

	@Override
	public RpcRequest read(Kryo kryo, Input input, Class<RpcRequest> type) 
	{
		RpcRequest rpcRequest;
		int id = input.readInt();
		byte scopeLength = input.readByte();
		byte[] scopeBytes = input.readBytes(scopeLength);
		byte methodLength = input.readByte();
		byte[] methodBytes = input.readBytes(methodLength);
		byte sigLength = input.readByte();
		byte[] sigBytes = input.readBytes(sigLength);
		String scopeName = new String(scopeBytes);
		String methodName = new String(methodBytes);
		String sigName = new String(sigBytes);
		Object[] args = (Object[])kryo.readClassAndObject(input);
		
		rpcRequest = new RpcRequest(id, scopeName, methodName, sigName, args);
		
		return rpcRequest;
	}
}
