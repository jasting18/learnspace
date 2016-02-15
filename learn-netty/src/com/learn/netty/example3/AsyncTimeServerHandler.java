package com.learn.netty.example3;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

public class AsyncTimeServerHandler implements Runnable {
	private int port;
	CountDownLatch latch;
	protected AsynchronousServerSocketChannel assc;
	
	public AsyncTimeServerHandler(int port) {
		this.port = port;
		try {
			assc = AsynchronousServerSocketChannel.open();
			assc.bind(new InetSocketAddress(port));
			System.out.println("The time server is start in port:"+port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void run() {
		latch = new CountDownLatch(1);
		doAccept();
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void doAccept(){
		assc.accept(this,new AcceptCompletionHandler());
	}
}
