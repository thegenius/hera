package com.lvonce.hera.provider;

import com.lvonce.hera.logger.RpcLogger;
import com.lvonce.hera.provider.Provider;
import com.lvonce.hera.context.RpcRequest;
import com.lvonce.hera.context.RpcMessage;
import com.lvonce.hera.context.RpcResponse;
import com.lvonce.hera.context.RpcMessageContext;
import com.lvonce.hera.exception.RpcException;
import com.lvonce.hera.exception.RpcExecuteException;
import com.lvonce.hera.exception.RpcTimeoutException;
import com.lvonce.hera.exception.RpcMethodNotFoundException;
import com.lvonce.hera.exception.RpcServiceNotFoundException;
import com.lvonce.hera.exception.RpcMethodArgsNotMatchException;
import com.lvonce.hera.asm.SignatureUtil;

import io.netty.channel.Channel;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.lang.reflect.Method;

public class ProviderManager {
	public static enum Type {
		ASM,
		REFLECTASM
	};	
	private final Type managerType;
	private final LinkedHashMap<String, Provider> serviceMap = new LinkedHashMap<String, Provider>();
	private final LinkedHashMap<String, LinkedHashSet<String>> signatureMap = new LinkedHashMap<String, LinkedHashSet<String>>();

	public ProviderManager(Type managerType) {
		this.managerType = managerType;
	}
	
	//public<T> void export(Class<T> serviceInterface, T serviceProvider) {
	//	this.export(serviceInterface, serviceProvider, null);
	//}

	public<T> void export(Class<T> serviceInterface, T serviceProvider, String serviceAlias) {
		String serviceName = serviceInterface.getName();
		Provider service = null;
		if (this.managerType.equals(Type.REFLECTASM)) {
			service = new ReflectasmProvider(serviceInterface, serviceProvider);
		} else if (this.managerType.equals(Type.ASM)) {
			service = new AsmProvider(serviceInterface, serviceProvider);
		}
		this.serviceMap.put(serviceName, service);
		if (serviceAlias != null) {
			this.serviceMap.put(serviceAlias, service);
		}

		Method[] methods = serviceInterface.getDeclaredMethods();
		for (Method method: methods) {
			String signatureKey = serviceName + "." + method.getName();
			LinkedHashSet<String> signatures = signatureMap.get(signatureKey); 
			if (signatures == null) {
				signatures = new LinkedHashSet<String>();
				signatureMap.put(signatureKey, signatures);
				if (serviceAlias != null) {
					String signatureKeyAlias = serviceAlias + "." + method.getName();
					signatureMap.put(signatureKeyAlias, signatures);
				}
			}
			signatures.add(SignatureUtil.getSignature(method));
		}
	}

	public void call(RpcMessageContext context) throws RpcException {
		RpcMessage message = context.getMessage();
		RpcLogger.debug(getClass(), "server received msg: " + message.toString());
		RpcRequest request = (RpcRequest)message.getContent();
		Channel channel = context.getChannel();
		int id = request.getId();

		String serviceName = request.getScopeName();
		Provider provider = this.serviceMap.get(serviceName);
		if (provider == null) {
			RpcException e = new RpcServiceNotFoundException(request.toString());	
			RpcResponse rpcResponse = new RpcResponse(id, e, false);
			RpcMessage resultMsg = new RpcMessage(1, rpcResponse);
			channel.writeAndFlush(resultMsg);
			return;
		}	

		String methodName = request.getMethodName();	
		String signatureKey = serviceName + "." + methodName;
		LinkedHashSet<String> signatures = signatureMap.get(signatureKey); 
		if (signatures == null) {
			RpcException e = new RpcMethodNotFoundException(request.toString());	
			RpcResponse rpcResponse = new RpcResponse(id, e, false);
			RpcMessage resultMsg = new RpcMessage(1, rpcResponse);
			channel.writeAndFlush(resultMsg);
			return;
		}

		String sigName = request.getSigName();
		//if (!signatures.contains(signature)) {
		//	throw new RpcMethodArgsNotMatchException(request.toString());	
		//}

		try {
			Object result = provider.call(methodName, sigName, request.getArgs());
			RpcResponse rpcResponse = new RpcResponse(id, result, true);
			RpcMessage resultMsg = new RpcMessage(1, rpcResponse);
			RpcLogger.debug(getClass(), "server result msg: " + resultMsg.toString());
			channel.writeAndFlush(resultMsg);
		} catch (Exception ex) {
			RpcException e = new RpcExecuteException(request.toString(), ex.getMessage());
			RpcResponse rpcResponse = new RpcResponse(id, e, false);
			RpcMessage resultMsg = new RpcMessage(1, rpcResponse);
			channel.writeAndFlush(resultMsg);
		}
	} 

}
