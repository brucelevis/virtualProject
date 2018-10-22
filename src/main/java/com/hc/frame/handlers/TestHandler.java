package com.hc.frame.handlers;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

public class TestHandler extends ChannelInboundHandlerAdapter{
	
	private final ByteBuf firstMessage;
	
	public TestHandler() {
		firstMessage = Unpooled.buffer();
		for(int i = 0; i < 5; i++) {
			firstMessage.writeByte(i);
		}
	}

	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("����serever handler channelActive����");
		//ctx.writeAndFlush(firstMessage);
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		ByteBuf mesg = (ByteBuf)msg;
		System.out.println("������channelRead");
		//������Ϊ�˲���
		try {
			String message = EncodeAndDecode.decode(mesg);
			System.out.println("����˽��յ�����ϢΪ��" + message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		//ctx.flush();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
	

}
