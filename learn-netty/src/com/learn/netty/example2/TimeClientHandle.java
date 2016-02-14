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
				selector.select(1000);
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
		
		//关闭多路复用器，所有注册的channel和Pipe等资源都会一并关闭，所以不需要重复释放资源
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
			//判断是否连接成功
			SocketChannel channel = (SocketChannel)key.channel();
			if (key.isConnectable()){
				if (channel.finishConnect()){
					channel.register(selector, SelectionKey.OP_READ);
					doWrite(channel);
				}else{
					System.exit(1);//连接失败，进程退出
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
				}else if (readBytes<0){
					//对端链路已关闭
					key.cancel();
					channel.close();
				}else{
					//读到0字节，忽略
				}
			}
			
		}
	}
	
	private void doConnect() throws IOException{
		//如果连接成功，那么注册到多路复用器上，发送请求消息，读应答
		if (socketChannel.connect(new InetSocketAddress(host,port))){
			socketChannel.register(selector, SelectionKey.OP_READ);
			doWrite(socketChannel);
		}else{
			socketChannel.register(selector, SelectionKey.OP_CONNECT);
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
