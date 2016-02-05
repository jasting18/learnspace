package com.learn.netty.example1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;

public class TimeClient {
	public static void main(String[] args){
		int num = 1000;
		CountDownLatch semaphore = new CountDownLatch(1);
		
		for (int i=0;i<num;i++){
			new Thread(new TimeQuery(semaphore)).start();
			System.out.println(i);
		}
		semaphore.countDown();
	}
}
