package com.learn.netty.example3;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsyncTimeServerHandler> {

	@Override
	public void completed(AsynchronousSocketChannel result, AsyncTimeServerHandler attachment) {
		attachment.assc.accept(attachment, this);
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		result.read(byteBuffer,byteBuffer,new ReadCompletionHandler(result));
	}

	@Override
	public void failed(Throwable exc, AsyncTimeServerHandler attachment) {
		System.out.println("Accept fail!");
		attachment.latch.countDown();
	}

}
