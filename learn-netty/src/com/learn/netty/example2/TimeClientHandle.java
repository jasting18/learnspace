package com.learn.netty.example2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class TimeClientHandle implements Runnable {
	private String host;
	private int port;
	private Selector selector;
	private SocketChannel socketChannel;
	private volatile boolean stop;
	
	public TimeClientHandle(String host,int port) {
		try {
			this.host = host;
			this.port = port;
			this.selector = Selector.open();
			this.socketChannel = SocketChannel.open();
			this.socketChannel.configureBlocking(false);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	@Override
	public void run() {
		try {
			doConnect();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		while(!stop){
			try {
				int selectNum = selector.select(1000);
				System.out.println("selected number is :"+selectNum);
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> it = selectedKeys.iterator();
				SelectionKey key = null;
				while(it.hasNext()){
					key = it.next();
					it.remove();
					
					try{
						handleInput(key);
					}catch(Exception e){
						e.printStackTrace();
						if (key.channel()!=null){
							key.channel().close();
						}
					}
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
	
	private void handleInput(SelectionKey key) throws IOException{
		if (key.isValid()){
			
			SocketChannel channel = (SocketChannel)key.channel();
			if (key.isConnectable()){
				if (channel.finishConnect()){
					channel.register(selector, SelectionKey.OP_READ);
					doWrite(channel);
				}else{
					System.exit(1);
				}
			}
			if (key.isReadable()){
				ByteBuffer readBuffer = ByteBuffer.allocate(1024);
				int readBytes = channel.read(readBuffer);
				if (readBytes>0){
					readBuffer.flip();
					byte[] bytes = new byte[readBuffer.remaining()];
					readBuffer.get(bytes);
					String body = new String(bytes,"UTF8");
					System.out.println("Now is :"+body);
					key.cancel();
					channel.close();
				}else if (readBytes<0){
					
					key.cancel();
					channel.close();
				}else{
					
				}
			}
			
		}
	}
	
	private void doConnect() throws IOException{
		
		if (socketChannel.connect(new InetSocketAddress(host,port))){
			socketChannel.register(selector, SelectionKey.OP_READ);
			doWrite(socketChannel);
		}else{
			socketChannel.register(selector, SelectionKey.OP_CONNECT);
			socketChannel.register(selector, SelectionKey.OP_READ);
		}
	}
	
	private void doWrite(SocketChannel sc) throws IOException{
		byte[] req = "query time".getBytes();
		ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
		writeBuffer.put(req);
		writeBuffer.flip();
		sc.write(writeBuffer);
		if (!writeBuffer.hasRemaining()){
			System.out.println("Send order to server success");
		}
	}

}
