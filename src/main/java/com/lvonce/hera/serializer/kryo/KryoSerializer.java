package com.lvonce.hera.serializer.kryo;
import com.lvonce.hera.serializer.NettySerializer;
import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

public class KryoSerializer implements NettySerializer {
	private static final byte[] LENGTH_PLACEHOLDER = new byte[4];


	/*
 	* 16-bits magic number
 	* 16-bits codec type
 	* 32-bits following message length
 	* messge
 	* */	
	public static void encapsulate(Object object, ByteBuf byteBuf) {
        try {
			Kryo kryo = KryoHolder.get();
			int startIdx = byteBuf.writerIndex();
        	ByteBufOutputStream byteOutputStream = new ByteBufOutputStream(byteBuf);

			short magicNumber = 0x4852;
			short encoderType = 0;
			byteOutputStream.writeShort(magicNumber);
			byteOutputStream.writeShort(encoderType);
			byteOutputStream.write(LENGTH_PLACEHOLDER);
			int bufferSize = 1024 * 4;
			int maxBufferSize = -1;
			Output output = new Output(bufferSize, maxBufferSize);
	        output.setOutputStream(byteOutputStream);
	        kryo.writeClassAndObject(output, object);
	        
	        output.flush();
	        output.close();
	        
	        int endIdx = byteBuf.writerIndex();
			int lengthOffset = startIdx + 4;
	        byteBuf.setInt(lengthOffset, endIdx - startIdx - 8);
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

	@Override
	public void serialize(Object object, ByteBufOutputStream ostream) {
		int bufferSize = 1024; // 1K for most message
		int maxBufferSize = -1; // -1 for no limit
		Output output = new Output(bufferSize, maxBufferSize);
	    output.setOutputStream(ostream);
		KryoHolder.get().writeClassAndObject(output, object);
	    output.flush();
	    output.close();
	}

	public static Object deserialize(ByteBuf buffer, int length) {
        Input input = new Input(new ByteBufInputStream(buffer, length));
        return KryoHolder.get().readClassAndObject(input);
	}	

	public static Object deserialize(ByteBuf buffer) {
		if (buffer == null) {
            return null;
		}
		
        Input input = new Input(new ByteBufInputStream(buffer));
        return KryoHolder.get().readClassAndObject(input);
	}	

	@Override
	public Object deserialize(ByteBufInputStream istream) {
        Input input = new Input(istream);
        return KryoHolder.get().readClassAndObject(input);
	}
}
