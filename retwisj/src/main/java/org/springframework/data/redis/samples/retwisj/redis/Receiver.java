package org.springframework.data.redis.samples.retwisj.redis;

import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

public class Receiver{
	
	public static void run(){
		BroadcastHandler handler = new BroadcastHandler();
		BroadcastService.Processor<BroadcastService.Iface> processor =
					new BroadcastService.Processor<BroadcastService.Iface>(handler);
		try{
			TNonblockingServerTransport serverTransport = new TNonblockingServerSocket(5052);
			TServer server = new TNonblockingServer(new TNonblockingServer.Args(serverTransport).processor(processor));
			System.out.println("starting receiver server...");
			server.serve();
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	static class BroadcastHandler implements BroadcastService.Iface {
				
	    @Override
	    public void send(String msg) {
			System.out.println(msg);
		}
	   
	}
}