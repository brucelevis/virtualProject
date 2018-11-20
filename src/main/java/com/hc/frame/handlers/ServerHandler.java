package com.hc.frame.handlers;


import com.hc.frame.Context;
import com.hc.frame.taskSchedule.TaskProducer;
import com.hc.logic.base.LogOut;
import com.hc.logic.base.Session;
import com.hc.logic.creature.Player;
import com.hc.logic.order.Order;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerHandler extends ChannelInboundHandlerAdapter{
	


    //private Session session;
    //���пͻ��˵�channel��session�Ķ�Ӧ,
    //private ConcurrentHashMap<Channel, Session> channel2Session = new ConcurrentHashMap<>();
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Session session = new Session();
		session.setChannel(ctx.channel());
		//��ֹ���½��Ŀͻ��˸�����ǰ�ͻ��˵�channel
		Context.addChannel2Session(ctx.channel(), session);
		System.out.println("����serever handler channelActive����");		
		Context.channelToString();		
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		ByteBuf mesg = (ByteBuf)msg;
		String order = "";
		try {
			order = EncodeAndDecode.decode(mesg);
			System.out.println("����˽��յ�����ϢΪ��" + order);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//��������
		String[] st = order.split(" ");

		Order.getService(st, Context.getSessionByChannel(ctx.channel()));
		

	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		//ctx.flush();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		//String name = Context.getSessionByChannel(ctx.channel()).getPlayer().getName();
		Player pl= Context.getSessionByChannel(ctx.channel()).getPlayer();
		String name = "";
		if(pl != null) {
			name = pl.getName();
			//������Ҷ���
			pl.brakLine();
			//����ĳһ���ͻ��˶Ͽ��󣬴������б��е�ɾ��
			Context.getOnlinPlayer().deletePlayer(pl);
			//����ĳһ���ͻ��˶Ͽ����������е���Ϣ
			pl.getScene().deletePlayer(pl);
			//ִ�еǳ�����, ���̳߳�����
			//new LogOut(pl).updateDB(pl);;
			System.out.println( "------------taskǰ");
			TaskProducer.addTask(new LogOut(pl));
			System.out.println( "------------task��");
		}		
	
		System.out.println( "�û���" + name + "���ѹر�����");
		//�ͻ��˶Ͽ�����ʱ����Ҫ�����channel-session��Context��ɾ����session�Ѿ������player��
		Context.deleteChannel2Session(ctx.channel());

		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		//System.out.println("----�ͻ����ѹر�����");
		super.channelInactive(ctx);
	}




	
	
	
}
