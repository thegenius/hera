package com.lvonce.hera.netty;

import com.lvonce.hera.logger.RpcLogger;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelInboundHandlerAdapter;

import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

@Sharable
public class AliveChecker extends ChannelInboundHandlerAdapter {

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

		if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {  
			IdleStateEvent event = (IdleStateEvent) evt;  
			if (event.state() == IdleState.READER_IDLE) {
			} else if (event.state() == IdleState.WRITER_IDLE) {
			} else if (event.state() == IdleState.ALL_IDLE) {
				ctx.close();
			}
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}
}

