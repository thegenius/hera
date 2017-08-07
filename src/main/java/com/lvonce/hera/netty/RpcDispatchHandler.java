package com.lvonce.hera.netty;

import com.lvonce.hera.logger.RpcLogger;
import com.lvonce.hera.worker.RpcWorkerService;
import com.lvonce.hera.context.RpcMessage;
import com.lvonce.hera.context.RpcMessageContext;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelInboundHandlerAdapter;

@Sharable
public class RpcDispatchHandler extends ChannelInboundHandlerAdapter {

	private final RpcWorkerService workerService;

	public RpcDispatchHandler(RpcWorkerService workerService) {
		this.workerService = workerService;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {		
		//RpcLogger.info(RpcDispatchHandler.class, "recieved msg: " + msg.toString());
		this.workerService.accept(new RpcMessageContext((RpcMessage)msg, ctx.channel()));
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		RpcLogger.info(RpcDispatchHandler.class, "channal active state: " + ctx.channel().isActive());
	}	
}
