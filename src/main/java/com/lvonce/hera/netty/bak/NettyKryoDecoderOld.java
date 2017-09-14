package com.lvonce.hera.netty;

import com.lvonce.hera.logger.RpcLogger;
import com.lvonce.hera.serializer.kryo.KryoSerializer;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class NettyKryoDecoderOld extends LengthFieldBasedFrameDecoder {		
	private static final int maxFrameLength = 1048576;
	private static final int lengthFieldOffset = 4;
	private static final int lengthFieldLength = 4;
	private static final int lengthAdjustment = 0;
	private static final int initialBytesToStrip = 8;

	//private static final int lengthFieldOffset = 0;
	//private static final int lengthFieldLength = 4;
	//private static final int lengthAdjustment = 0;
	//private static final int initialBytesToStrip = 4;

	public NettyKryoDecoderOld() {
        super(maxFrameLength, 
			lengthFieldOffset, 
			lengthFieldLength, 
			lengthAdjustment, 
			initialBytesToStrip);
    }
	
	@Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		RpcLogger.info(getClass(), "NettyKryoDecoder decode()");
 		short magicNumber = in.getShort(0);
		short decoderType = in.getShort(2);
		if (magicNumber == 0x4852) { 
			RpcLogger.info(getClass(), "hera protocol");
		}
		RpcLogger.info(getClass(), "decoder type:" + decoderType);

        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
		RpcLogger.info(getClass(), "11111111111111!!!!!!!");
        if (frame == null) {
			RpcLogger.info(getClass(), "null frame !!!!!!!");
            return null;
		}
		RpcLogger.info(getClass(), "22222222222222!!!!!!!");
        return KryoSerializer.deserialize(frame, frame.readableBytes());
        //byte[] bufferArray = frame.array();
		//RpcLogger.info(getClass(), "333333333333333333!!!!!");
		//int offset = frame.arrayOffset();
		//RpcLogger.info(getClass(), "444444444444444!!!!!");
		//int count = frame.readableBytes();
		//RpcLogger.info(getClass(), "array" + bufferArray + "offset:" + offset + "count:" + count);
        //return KryoSerializer.deserialize(bufferArray, offset, count);
    }

    @Override
    protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
		RpcLogger.debug(getClass(), "slice with index:" +index +" length:"+length); 
        return buffer.slice(index, length);
    }
}
