package com.hc.frame.handlers;

import io.netty.channel.ChannelInboundHandlerAdapter;

import com.hc.frame.ClientContext;
import com.hc.frame.swing.CommandAction;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

public class ClientDisplayHandler extends ChannelInboundHandlerAdapter{

	CommandAction commandAction;

	
	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		commandAction = CommandAction.getInstance();
		System.out.println("������ClientH��channelActive");
	}

	//���մӷ���˷�������Ϣ����������ʾ
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
	     System.out.println("�����ǿͻ��˵�channelRead");
	     //��Ҫ����ص�msg���ݵ�ת��
	     try {
			String toDisplay = EncodeAndDecode.decode(msg);
			//������ʾ���������͵�����
			CommandAction.backDisplay(toDisplay);
		    //������Ҫ�޸�
		    //CommandAction ac = ClientContext.getMap().get(ctx.channel());
		    //ac.backDisplay(toDisplay);
		    
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
