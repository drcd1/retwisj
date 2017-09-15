package org.springframework.data.redis.samples.retwisj.replication;

import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.springframework.data.redis.samples.retwisj.command.*;
import org.springframework.data.redis.samples.retwisj.redis.RetwisRepository;

public class ReceiverThrift{
	
	private static RetwisRepository retwis;
	public static void setRetwis(RetwisRepository otherRetwis){
		retwis = otherRetwis;
	}
	public static void run(){		
		BroadcastHandler handler = new BroadcastHandler();
		BroadcastService.Processor<BroadcastService.Iface> processor =
					new BroadcastService.Processor<BroadcastService.Iface>(handler);
		
		try{
			TServerTransport serverTransport = new TServerSocket(5052);
			TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));
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
	    		retwis.execute(CommandFactory.get(new CommandData(CommandData.getTypeFromInt(
	    															cmd.getCmd()),
	    															cmd.getArguments())
	    									)
	    		);
	    	} catch (Exception e){
	    		e.printStackTrace();
	    	}
	    }
	   
	}
}