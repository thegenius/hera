package com.lvonce.hera.netty;

import com.lvonce.hera.logger.RpcLogger;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelHandlerContext;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;

import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;


@Sharable
public class AliveChecker extends ChannelInboundHandlerAdapter {
	//private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(
	//		Unpooled.copiedBuffer("Heartbeating.../n", CharsetUtil.UTF_8)); 

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

		if (evt instanceof IdleStateEvent) {  
			IdleStateEvent event = (IdleStateEvent) evt;  
			String type = "";
			if (event.state() == IdleState.READER_IDLE) {
				RpcLogger.info(AliveChecker.class, ctx.channel().remoteAddress() + " read timeout!");
				ctx.close();
			} else if (event.state() == IdleState.WRITER_IDLE) {
				RpcLogger.info(AliveChecker.class, ctx.channel().remoteAddress() + " write timeout!");
				ctx.close();
			} else if (event.state() == IdleState.ALL_IDLE) {
				RpcLogger.info(AliveChecker.class, ctx.channel().remoteAddress() + " unknown timeout!");
				ctx.close();
			}

			//ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate()).addListener(
			//		ChannelFutureListener.CLOSE_ON_FAILURE);  
 
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}
}

