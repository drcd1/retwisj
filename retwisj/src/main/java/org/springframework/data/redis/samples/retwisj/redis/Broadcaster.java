package org.springframework.data.redis.samples.retwisj.redis;
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

public class Broadcaster{
	private static HashSet<BroadcastService.Client> replicas = new HashSet<BroadcastService.Client>();
	
	public static void run(){
		String retAddr = System.getenv("RET_LINKS");
		for(String addr: retAddr.split(":")){
			log(addr);
		}
	}
	
	public static void log(String hostAddr) {
	   	try {
	   		System.out.println("Will add " + hostAddr);
			TTransport transport = new TFramedTransport(new TSocket(hostAddr, 5052));
			while(!openTransport(transport)){
				System.out.println("Sleeping...");
					
				TimeUnit.SECONDS.sleep(5);
					
			}

				
			TProtocol protocol = new TBinaryProtocol(transport);
			BroadcastService.Client cl = new BroadcastService.Client(protocol);
			replicas.add(cl);
			
			System.out.println("Added " + hostAddr);
		}catch(Exception e){
			e.printStackTrace();
		}
	
	   
	}
	
	private static boolean openTransport(TTransport transport){
		try{
			transport.open();
			return true;
		} catch(Exception e){
			return false;
		}
	}
	
	
	//should return sucess/failure?
	public static void broadcast(){
		for(BroadcastService.Client cl: replicas){
			try{
				cl.send(System.getenv("MY_NAME") + " says hi!" );
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}