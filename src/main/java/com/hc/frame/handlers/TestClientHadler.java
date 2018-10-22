package com.hc.frame.handlers;


import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.buffer.Unpooled;

public class TestClientHadler extends ChannelInboundHandlerAdapter{
	
	private final ByteBuf firstMessage;
	
	public TestClientHadler() {
		firstMessage = Unpooled.buffer();
		for(int i = 0; i < 5; i++) {
			firstMessage.writeByte(i);
		}
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		System.out.println("������ClientH��channelActive");
		ctx.writeAndFlush(firstMessage);
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
	     System.out.println("�����һ���ͻ���handler");
	     ctx.fireChannelRead(msg);
	     //ctx.write(msg);
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
