package com.learn.netty.example2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ServerHeartBeat implements Runnable {
	private SocketChannel sc;
	private int seq = 0;
	public ServerHeartBeat(SocketChannel sc) {
		this.sc = sc;
	}
	@Override
	public void run() {
		while(true){
			seq = seq+1;
			String body = new Integer(seq).toString();
			byte[] bytes = body.getBytes();
			ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
			writeBuffer.put(bytes);
			writeBuffer.flip();
			try {
				sc.write(writeBuffer);
			} catch (IOException e) {
				e.printStackTrace();
				try {
					sc.close();
					break;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
