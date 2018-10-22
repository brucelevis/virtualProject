package com.hc.frame.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * ���дӿͻ��˷�������Ϣ
 * @author hc
 *
 */
public class ClientSender extends ChannelOutboundHandlerAdapter{
	
	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
		String message = (String)msg;
		ByteBuf bf = EncodeAndDecode.encode(message);
		ctx.write(bf);
		ctx.flush();
		System.out.println("clientSender ��ʼ����");
	}
	

	
	
}
