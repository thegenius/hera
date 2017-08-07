package com.lvonce.hera.worker;

import com.lvonce.hera.future.RpcFuture;
import com.lvonce.hera.logger.RpcLogger;
import com.lvonce.hera.provider.ProviderManager;

import com.lvonce.hera.context.RpcRequest;
import com.lvonce.hera.context.RpcResponse;
import com.lvonce.hera.context.RpcMessage;
import com.lvonce.hera.context.RpcMessageContext;

import com.lvonce.hera.handler.RpcMessageHandler;
import com.lvonce.hera.handler.RpcRequestHandler;
import com.lvonce.hera.handler.RpcResponseHandler;

import com.lvonce.hera.exception.RpcException;
import com.lvonce.hera.exception.RpcExecuteException;
import com.lvonce.hera.exception.RpcMethodNotFoundException;
import com.lvonce.hera.exception.RpcServiceNotFoundException;
import com.lvonce.hera.exception.RpcMethodArgsNotMatchException;

import java.util.LinkedHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;

import java.lang.InterruptedException;
import java.lang.IllegalArgumentException;

import io.netty.channel.Channel;
import com.esotericsoftware.reflectasm.MethodAccess;

public class RpcWorkerThreadPool implements RpcWorkerService {
	
	private final ConcurrentMap<Integer, RpcFuture> invokeIdRpcFutureMap = new ConcurrentHashMap<Integer, RpcFuture>();
	private final BlockingQueue<RpcMessageContext> messageQueue = new LinkedBlockingQueue<RpcMessageContext>();
	private final ExecutorService threadPool;
	private final ProviderManager providerManager;
	private final RpcMessageHandler[] handlers;

	public RpcWorkerThreadPool(ExecutorService threadPool) {
		this.threadPool = threadPool;
		//this.providerManager = new ProviderManager(ProviderManager.Type.REFLECTASM);
		this.providerManager = new ProviderManager(ProviderManager.Type.ASM);
		this.handlers = new RpcMessageHandler[] { 
			new RpcRequestHandler(this.providerManager),
			new RpcResponseHandler(invokeIdRpcFutureMap)
		};
	}


	public void start(int workers) {
		for(int i=0; i<workers; ++i) {
			threadPool.execute(
				()->{
					RpcMessageContext context = null;
					while (true) {
						try {
							context = this.messageQueue.take();
							RpcMessage message = context.getMessage();
							int messageType = message.getMessageType();
							handlers[messageType].accept(context);	
						} catch (RpcException e) {
							//int id = request.getId();
							//RpcResponse rpcResponse = new RpcResponse(id, e, false);
							//Channel channel = request.getChannel();
							//channel.writeAndFlush(rpcResponse);
						} catch (Exception e) {
							//e.printStackTrace();
							//int id = request.getId();
							//RpcExecuteException ex = new RpcExecuteException(request.getRpcRequest().toString(), e.toString());
							//RpcResponse rpcResponse = new RpcResponse(id, e, false);
							//Channel channel = request.getChannel();
							//channel.writeAndFlush(rpcResponse);
						}
					}			
				}
			);
		}
	}
	
	public void register(int id, RpcFuture rpcFuture) {
		invokeIdRpcFutureMap.put(id, rpcFuture);
	}
	
	public<T> void export(Class<T> interfaceClass, T serviceProvider) {
		this.providerManager.export(interfaceClass, serviceProvider, null);
	}
	
	public<T> void export(Class<T> interfaceClass, T serviceProvider, String serviceAlias) {
		this.providerManager.export(interfaceClass, serviceProvider, serviceAlias);
	}

	public void accept(RpcMessageContext message) {
		try {
			messageQueue.put(message);
		} catch (InterruptedException e) {
			RpcLogger.info(getClass(), e.getMessage());
		}
	}
}
