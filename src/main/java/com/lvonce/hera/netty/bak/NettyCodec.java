package com.lvonce.hera.netty;

import io.netty.buffer.ByteBuf;

public interface NettyCodec {
	/* write the object into buffer */
	void encode(Object object, ByteBuf buffer);

	/* read the buffer of specified length, decode it into object */
	Object decode(ByteBuf buffer, int length);	
}
