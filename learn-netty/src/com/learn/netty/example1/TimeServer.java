package com.learn.netty.example1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TimeServer {
	public static void main(String[] args) throws IOException{
		int port = 8080;
		
		ServerSocket server = null;
		try{
			server = new ServerSocket(port);
			System.out.println("time server start on port "+port);
			
			Socket socket = null;
			while(true){
				socket = server.accept();
				new Thread(new TimeServerHandler(socket)).start();
				System.out.println("handle socket.");
			}
		}finally{
			if (server!=null){
				server.close();
				System.out.println("time server close");
				server = null;
			}
		}
	}
}
