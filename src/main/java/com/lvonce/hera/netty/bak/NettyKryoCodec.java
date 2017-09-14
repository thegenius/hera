package com.lvonce.hera.netty;

import java.io.IOException;
import io.netty.buffer.ByteBuf;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import com.lvonce.hera.serializer.kryo.KryoHolder;

public class NettyKryoCodec implements NettyCodec {

	@Override
	public void encode(Object object, ByteBuf buffer) {
		int bufferSize = 1024; // 1K for most message
		int maxBufferSize = -1; // -1 for no limit
		Output output = new Output(bufferSize, maxBufferSize);
	    output.setOutputStream(new ByteBufOutputStream(buffer));
		KryoHolder.get().writeClassAndObject(output, object);
	}

	@Override
	public Object decode(ByteBuf buffer, int length) {
        Input input = new Input(new ByteBufInputStream(buffer, length));
        return KryoHolder.get().readClassAndObject(input);
	}	
}
