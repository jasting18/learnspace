package com.learn.netty.example3;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Date;

public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
	private AsynchronousSocketChannel channel;
	
	public ReadCompletionHandler(AsynchronousSocketChannel channel) {
		if (this.channel==null)
			this.channel = channel;
	}
	@Override
	public void completed(Integer result, ByteBuffer attachment) {
		attachment.flip();
		byte[] body = new byte[attachment.remaining()];
		attachment.get(body);
		
		try {
			String req = new String(body,"UTF-8");
			System.out.println("The time server receive order is :"+req);
			String currentTime = "query time".equals(body)?new Date(System.currentTimeMillis()).toString():"bad order";
			doWrite(currentTime);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	private void doWrite(String body){
		if (body==null||body.trim().length()==0)return;
		
		byte[] bytes = body.getBytes();
		ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
		writeBuffer.put(bytes);
		writeBuffer.flip();
		channel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer,ByteBuffer>(){

			@Override
			public void completed(Integer result, ByteBuffer attachment) {
				//���û�з�����ɣ���������
				if (attachment.hasRemaining()){
					channel.write(attachment,attachment,this);
				}
			}

			@Override
			public void failed(Throwable exc, ByteBuffer attachment) {
				try {
					channel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		});
	}

	@Override
	public void failed(Throwable exc, ByteBuffer attachment) {
		try {
			this.channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
