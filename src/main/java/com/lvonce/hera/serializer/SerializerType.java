package com.lvonce.hera.serializer;

public enum SerializerType {
	KRYO,
	PROTOBUF;
	
	public static SerializerType getDefault() {
		//return KRYO;
		return PROTOBUF;
	}
};
