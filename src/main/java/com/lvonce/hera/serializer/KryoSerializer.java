package com.lvonce.hera.serializer;

import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

public class KryoSerializer {
	private static final byte[] LENGTH_PLACEHOLDER = new byte[4];
	
	public static void serializeWithLengthField(Object object, ByteBuf byteBuf) {
		Kryo kryo = KryoHolder.get();
		int startIdx = byteBuf.writerIndex();
        ByteBufOutputStream byteOutputStream = new ByteBufOutputStream(byteBuf);
        try {
			byteOutputStream.write(LENGTH_PLACEHOLDER);
			int bufferSize = 1024 * 4;
			int maxBufferSize = -1;
			Output output = new Output(bufferSize, maxBufferSize);
	        output.setOutputStream(byteOutputStream);
	        kryo.writeClassAndObject(output, object);
	        
	        output.flush();
	        output.close();
	        
	        int endIdx = byteBuf.writerIndex();
	        byteBuf.setInt(startIdx, endIdx - startIdx - 4);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void serialize(Object object, ByteBuf buffer) {
		int bufferSize = 1024; // 1K for most message
		int maxBufferSize = -1; // -1 for no limit
		Output output = new Output(bufferSize, maxBufferSize);
	    output.setOutputStream(new ByteBufOutputStream(buffer));
		
		KryoHolder.get().writeClassAndObject(output, object);
	    output.flush();
	    output.close();
	}

	public static Object deserialize(ByteBuf buffer) {
		if (buffer == null) {
            return null;
		}
		
        Input input = new Input(new ByteBufInputStream(buffer));
        return KryoHolder.get().readClassAndObject(input);
	}	
}
