package com.learn.netty.example3;

public class TimeServer {
	public static void main(String[] args){
		int port = 8080;
		AsyncTimeServerHandler timeServer = new AsyncTimeServerHandler(port);
		new Thread(timeServer).start();
	}
}
