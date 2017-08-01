package org.springframework.data.redis.samples.retwisj;

import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.springframework.data.redis.samples.retwisj.redis.RetwisRepository;


import org.springframework.data.redis.samples.retwisj.command.*;

public class Receiver{
	
	private static RetwisRepository retwis;
	
	public static void run(RetwisRepository otherRetwis){
		retwis = otherRetwis;
		
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
	    public void send(BroadcastCommand cmd) {
	    	try{
	    		retwis.execute(CommandFactory.get(cmd));
	    	} catch (Exception e){
	    		e.printStackTrace();
	    	}
	    }
	   
	}
}