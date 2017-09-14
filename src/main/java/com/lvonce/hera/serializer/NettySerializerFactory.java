package com.lvonce.hera.serializer;

import com.lvonce.hera.serializer.kryo.KryoSerializer;
import com.lvonce.hera.serializer.protobuf.ProtobufSerializer;

public class NettySerializerFactory {

	private static final NettySerializer[] serializers = {
		new KryoSerializer(),
		new ProtobufSerializer()
	};
	
	public static NettySerializer get(SerializerType type) {
		return serializers[type.ordinal()]; 
	}

	public static NettySerializer getDefault() {
		return get(SerializerType.getDefault());
	}
}
