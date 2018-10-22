package com.hc.frame.handlers;

import com.hc.frame.Context;
import com.hc.logic.base.Session;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class ServerSender extends ChannelOutboundHandlerAdapter{

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
		
		String message = (String)msg;
		ByteBuf bf = EncodeAndDecode.encode(message);
		ctx.write(bf);
		ctx.flush();
		System.out.println("ServerSender ��ʼ����");
		//System.out.println("channel�� " + Session.getChannel() + " ctx�ǣ� " + ctx.toString());
		
	}
	
}
