package com.lvonce.hera.netty;

import com.lvonce.hera.context.RpcMessage;
import com.lvonce.hera.context.RpcMessageContext;
import com.lvonce.hera.serializer.SerializerType;
import com.lvonce.hera.serializer.NettySerializer;
import com.lvonce.hera.serializer.NettySerializerFactory;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.LengthFieldPrepender;

@Sharable
public class NettyEncoder extends MessageToByteEncoder<Object> {	

	@Override
	protected void encode(ChannelHandlerContext ctx, Object obj, ByteBuf out) throws Exception {
		RpcMessageContext msgCtx = (RpcMessageContext)obj;
		RpcMessage msg = msgCtx.getMessage();
		SerializerType serializerType = msgCtx.getSerializerType();
		
		int startIdx = out.writerIndex();
    	ByteBufOutputStream ostream = new ByteBufOutputStream(out);
		short magicNumber = 0x4852;
		ostream.writeShort(magicNumber);
		ostream.writeShort(serializerType.ordinal());
		ostream.writeInt(0);
		NettySerializerFactory.get(serializerType).serialize(msg, ostream);
		int endIdx = out.writerIndex();
		int lengthOffset = startIdx + 4;
	    out.setInt(lengthOffset, endIdx - startIdx - 8);
	}
}

