package com.hc.frame.handlers;

import java.io.UnsupportedEncodingException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class EncodeAndDecode {

	/**
	 * ����
	 * ���ͻ��˺ͷ����֮��ͨ�ŵ���Ϣ���н��룬
	 * ����ByteBuf -> String ����ת��
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	public static String decode(Object msg) throws Exception{
		ByteBuf buf = (ByteBuf)msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);	
		String body = new String(req, "UTF-8"); //��ֹ��������
		return body;
	}
	
	/**
	 * ����
	 * ��String -> ByteBuf����ת��
	 * @param st
	 * @return
	 */
	public static ByteBuf encode(String st) {
		ByteBuf bf = Unpooled.buffer(st.length());
		byte[] result;
		try {
			result = st.getBytes("UTF-8");  //��ֹ��������
			bf.writeBytes(result);
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}  
		return bf;
	}
	
}
