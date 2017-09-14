package com.lvonce.hera.serializer;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

public interface NettySerializer {

	/*serialize an object to buffer
 	* @param object : the object you want to serialize	
 	* @param ostream : the output stream you need to write
 	* */
	public void serialize(Object object, ByteBufOutputStream ostream);


	/*deserialize a buffer with specific length into an object
 	* @param istream : the input stream contains the binary data of the object
 	* @return : return the object deserialize from the buffer
 	* */
	public Object deserialize(ByteBufInputStream istream);
}

