package com.learn.netty.example2;

import java.util.concurrent.CountDownLatch;

public class TimeClient {
	public static void main(String[] args){
		int num = 1000;
//		CountDownLatch semaphore = new CountDownLatch(1);
//		
//		for (int i=0;i<num;i++){
//			new Thread(new TimeQuery(semaphore)).start();
//			System.out.println(i);
//		}
//		semaphore.countDown();
		
		TimeClientHandle clientHandle = new TimeClientHandle("10.128.221.42",8080);
		//TimeClientHandle clientHandle = new TimeClientHandle("localhost",8080);
		new Thread(clientHandle).start();
	}
}
