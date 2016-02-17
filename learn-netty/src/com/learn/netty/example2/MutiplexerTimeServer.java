package com.learn.netty.example2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class MutiplexerTimeServer implements Runnable {
	private Selector selector;
	private ServerSocketChannel ssChannel;
	private volatile boolean stop = false;
	
	public MutiplexerTimeServer(int port) {
		try {
			selector = selector.open();
			ssChannel = ServerSocketChannel.open();
			ssChannel.configureBlocking(false);
			ssChannel.socket().bind(new InetSocketAddress(port), 1024);
			ssChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("the time server is start on port:"+port);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void stop(){
		this.stop = true;
	}
	@Override
	public void run() {
		while(!stop){
			try {
				int selectNum = selector.select(1000);
				//System.out.println("selected number is :"+selectNum);
				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> it = keys.iterator();
				SelectionKey key = null;
				while(it.hasNext()){
					key = it.next();
					it.remove();
					handleInput(key);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (selector!=null){
			try {
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void handleInput(SelectionKey key) throws IOException{
		if (key.isValid()){
			if (key.isAcceptable()){
				//Accept the new connection
				ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
				SocketChannel sc = ssc.accept();
				sc.configureBlocking(false);
				sc.register(selector, SelectionKey.OP_READ);
			}
			if (key.isReadable()){
				//Read the data
				SocketChannel sc = (SocketChannel)key.channel();
				ByteBuffer readBuffer = ByteBuffer.allocate(1024);
				
				int readBytes = 0;
				try {
					readBytes = sc.read(readBuffer);
				} catch (Exception e) {
					e.printStackTrace();
					key.cancel();
					sc.close();
				}
				if (readBytes>0){
					readBuffer.flip();
					byte[] bytes = new byte[readBuffer.remaining()];
					readBuffer.get(bytes);
					String body = new String(bytes,"UTF-8");
					System.out.println("The time server receive order:"+body);
					String currentTime = "query time".equals(body)?new Date(System.currentTimeMillis()).toString():"bad order";
					doWrite(sc,currentTime);
				}else if (readBytes<0){
					//对端链路关闭
					key.cancel();
					sc.close();
				}else{
					//读到0字节，忽略
				}
			}
		}
	}
	
	private void doWrite(SocketChannel sc,String response) throws IOException{
		if (response!=null && response.trim().length()>0){
			byte[] bytes = response.getBytes();
			ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
			writeBuffer.put(bytes);
			writeBuffer.flip();
			sc.write(writeBuffer);
		}
	}
	

}
