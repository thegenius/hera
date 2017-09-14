package com.lvonce.hera.serializer.protobuf;

import com.lvonce.hera.logger.RpcLogger;
import com.lvonce.hera.context.RpcMessage;
import com.lvonce.hera.serializer.NettySerializer;

import io.protostuff.Schema;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.runtime.RuntimeSchema;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

public class ProtobufSerializer implements NettySerializer {
	private static final Schema<RpcMessage> schema = RuntimeSchema.getSchema(RpcMessage.class);

	private static final ThreadLocal<LinkedBuffer> localBuffer = new ThreadLocal<LinkedBuffer>() {
		protected LinkedBuffer initialValue() {
			return LinkedBuffer.allocate();
		}
	};

	@Override
	public void serialize(Object object, ByteBufOutputStream ostream) {
		RpcLogger.info(getClass(), "protobuf serialize");
		RpcMessage msg = (RpcMessage) object;
		LinkedBuffer buffer = localBuffer.get();
		try {
			ProtobufIOUtil.writeTo(ostream, msg, schema, buffer);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			buffer.clear();	
		}			
	}


	@Override
	public Object deserialize(ByteBufInputStream istream) {
		RpcLogger.info(getClass(), "protobuf deserialize");
		RpcMessage msg = schema.newMessage();
		LinkedBuffer buffer = localBuffer.get();
		try {
			ProtobufIOUtil.mergeFrom(istream, msg, schema, buffer);
			return msg; 
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			buffer.clear();
		}
	}

}
