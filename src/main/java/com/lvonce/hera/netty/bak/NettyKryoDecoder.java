package com.lvonce.hera.netty;

import com.lvonce.hera.logger.RpcLogger;
import com.lvonce.hera.context.RpcMessage;
import com.lvonce.hera.context.RpcMessageContext;
import com.lvonce.hera.serializer.SerializerType;
import com.lvonce.hera.serializer.kryo.KryoSerializer;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class NettyKryoDecoder extends ByteToMessageDecoder {	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
 		short magicNumber = in.readShort();
		short decoderType = in.readShort();
		if (magicNumber == 0x4852) { 
			RpcLogger.info(getClass(), "decoder type:" + decoderType);
			int messageLength = in.readInt();
			if (in.isReadable(messageLength)) {
				RpcMessage message = (RpcMessage)KryoSerializer.deserialize(in, messageLength);
				RpcMessageContext context = new RpcMessageContext(message, ctx.channel(), SerializerType.values()[decoderType]);
				out.add(context);
			}
		}
	}
}
