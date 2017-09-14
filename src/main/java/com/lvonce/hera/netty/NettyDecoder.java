package com.lvonce.hera.netty;

import com.lvonce.hera.logger.RpcLogger;
import com.lvonce.hera.serializer.SerializerType;
import com.lvonce.hera.serializer.NettySerializer;
import com.lvonce.hera.serializer.NettySerializerFactory;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class NettyDecoder extends ByteToMessageDecoder {	

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
 		short magicNumber = in.readShort();
		short decoderType = in.readShort();
		if (magicNumber == 0x4852) { 
			RpcLogger.info(getClass(), "decoder type:" + decoderType);
			int messageLength = in.readInt();
			RpcLogger.info(getClass(), "message length:" + messageLength);
			if (in.isReadable(messageLength)) {
				NettySerializer serializer = NettySerializerFactory.get(SerializerType.values()[decoderType]);
				Object message = serializer.deserialize(new ByteBufInputStream(in, messageLength));
				out.add(message);
			}
		}
	}
}
