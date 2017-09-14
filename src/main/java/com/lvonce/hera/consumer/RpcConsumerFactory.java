package com.lvonce.hera.consumer;

import com.lvonce.hera.logger.RpcLogger;
import com.lvonce.hera.asm.RouterGenerator;
import com.lvonce.hera.consumer.jdk.RpcJDKConsumer;
import com.lvonce.hera.serializer.SerializerType;
import java.lang.reflect.Proxy;

public class RpcConsumerFactory {
	public enum Type {
		ASM_PROXY,
		ASM_ASYNC_PROXY,
		JDK_PROXY
	}
		
	public static <T> T create(RpcConsumerFactory.Type consumerType, Class<T> targetClass, int timeoutMills, String host, int port) {
		return create(SerializerType.getDefault(), consumerType, targetClass, timeoutMills, host, port);
	}

	public static <T> T create(SerializerType serializerType, RpcConsumerFactory.Type consumerType, Class<T> targetClass, int timeoutMills, String host, int port) {
		try {
			if (consumerType.equals(Type.ASM_PROXY)) {
				RpcNodeConsumer consumer = new RpcNodeConsumer(serializerType, targetClass.getName(), host, port, timeoutMills);
				return (T)RouterGenerator.getJoinRouter(targetClass, consumer, "invoke");
			}
			if (consumerType.equals(Type.ASM_ASYNC_PROXY)) {
				RpcNodeConsumer consumer = new RpcNodeConsumer(serializerType, targetClass.getName(), host, port, timeoutMills);
				return (T)RouterGenerator.getJoinRouter(targetClass, consumer, "call");
			}
			if (consumerType.equals(Type.JDK_PROXY)) {
				RpcJDKConsumer rpcClient = new RpcJDKConsumer(targetClass.getName(), host, port, timeoutMills);
				return (T)Proxy.newProxyInstance(targetClass.getClassLoader(), new Class<?>[]{targetClass}, rpcClient);
			}
		} catch (NoSuchMethodException e) {
			RpcLogger.debug(RpcConsumerFactory.class, "no such method");
			RpcLogger.debug(RpcConsumerFactory.class, e.getMessage());
		}
		return null;
	}
}
