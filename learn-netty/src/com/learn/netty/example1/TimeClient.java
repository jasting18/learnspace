package com.learn.netty.example1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class TimeClient {
	public static void main(String[] args){
		int port  = 8080;
		String serverIp = "10.128.221.54";
		
		Socket socket = null;
		BufferedReader in = null;
		PrintWriter out = null;
		
		try {
			socket = new Socket(serverIp,port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(),true);
			out.println("query time");
			System.out.println("send order to server success.");
			String resp = in.readLine();
			System.out.println("Time is :"+resp);
		} catch (UnknownHostException e) {
			e.printStackTrace();
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
				if (socket!=null){
					try {
						socket.close();
						socket = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
