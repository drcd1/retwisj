package org.springframework.data.redis.samples.retwisj.replication;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import org.springframework.data.redis.samples.retwisj.command.*;

public class BroadcasterThrift extends Broadcaster {

	private HashSet<String> replicas = new HashSet<String>();
	
	
	void log(String hostAddr) {
		try {
			System.out.println("Will add " + hostAddr);
			
			replicas.add(hostAddr);
				
			System.out.println("Added " + hostAddr);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	  
	
	private boolean openTransport(TTransport transport){
		try{
			transport.open();
			return true;
		} catch(Exception e){
			return false;
		}
	}
	
	
	//should return sucess/failure?
	public void broadcast(CommandData data){
		BroadcastCommand cmd = new BroadcastCommand(CommandData.getIntFromType(data.getCmd()),
													data.getArguments());
		for(String replica: replicas){			
			ThreadMethod r = new ThreadMethod(replica, cmd);
			new Thread(r).start();
		}
	}
	
	class ThreadMethod implements Runnable{
		ThreadMethod(String replica, BroadcastCommand cmd){
			this.replica = replica;
			this.cmd = cmd;
		}
		private String replica;
		private BroadcastCommand cmd;
		
		public void run(){
			try{
				TTransport transport = new TSocket(replica, 5052);
				while(!openTransport(transport)){
					System.out.println("Sleeping...");
					
					TimeUnit.SECONDS.sleep(5);
						
				}					
				TProtocol protocol = new TBinaryProtocol(transport);
				BroadcastService.Client cl = new BroadcastService.Client(protocol);
				
				cl.send(cmd);
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	};
}
