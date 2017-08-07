package com.lvonce.hera.netty;

import com.lvonce.hera.serializer.KryoSerializer;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class NettyKryoDecoder extends LengthFieldBasedFrameDecoder {		
	private static final int maxFrameLength = 1048576;
	private static final int lengthFieldOffset = 0;
	private static final int lengthFieldLength = 4;
	private static final int lengthAdjustment = 0;
	private static final int initialBytesToStrip = 4;

	public NettyKryoDecoder() {
        super(maxFrameLength, 
			lengthFieldOffset, 
			lengthFieldLength, 
			lengthAdjustment, 
			initialBytesToStrip);
    }
	
	@Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
		}
        return KryoSerializer.deserialize(frame);
    }

    @Override
    protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
        return buffer.slice(index, length);
    }
}
