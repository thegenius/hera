package com.lvonce.hera.netty;

import com.lvonce.hera.serializer.kryo.KryoSerializer;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.LengthFieldPrepender;

@Sharable
public class NettyKryoEncoder extends MessageToByteEncoder<Object> {	
	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
		KryoSerializer.encapsulate(msg, out);
	}
}

//public class NettyKryoEncoder extends LengthFieldPrepender {	
//	private static final int lengthFieldLength = 4;
//	public NettyKryoEncoder() {
//		super(lengthFieldLength);
//	}
//
//	@Override
//	protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
//		KryoSerializer.serialize(msg, out);
//		super.encode(ctx, msg, out);
//	}
//}
