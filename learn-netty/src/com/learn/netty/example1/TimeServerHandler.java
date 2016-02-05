package com.learn.netty.example1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

public class TimeServerHandler implements Runnable{
	private Socket socket = null;
	
	public TimeServerHandler(Socket socket){
		this.socket = socket;
	}
	@Override
	public void run() {
		BufferedReader in = null;
		PrintWriter out = null;
		
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(this.socket.getOutputStream(),true);
			String currentTime = null;
			String body = null;
			
			while(true){
				body = in.readLine();
				if (body==null)break;
				
				System.out.println("The time server receive order:"+body);
				currentTime = "query time".equals(body)?new Date(System.currentTimeMillis()).toString():"bad order";
				out.println(currentTime);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if (in!=null){
				try {
					in.close();
					in = null;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				if (out!=null){
					out.close();
					out = null;
				}
				if (this.socket!=null){
					try {
						this.socket.close();
						this.socket = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}

}
