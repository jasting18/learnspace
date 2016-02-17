package com.learn.netty.example2;

public class TimeServer {
	public static void main(String[] args){
		int port = 8080;
		
		MutiplexerTimeServer timeServer = new MutiplexerTimeServer(port);
		new Thread(timeServer).start();
	}
	
}
