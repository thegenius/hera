package com.lvonce.hera.serializer;

import io.netty.buffer.ByteBuf;

public interface NettySerializer {
	public void serializeWithLengthField(Object object, ByteBuf buffer);	
	public void serialize(Object object, ByteBuf buffer);
	public Object deserialize(ByteBuf buffer);
}

