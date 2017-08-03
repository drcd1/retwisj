package org.springframework.data.redis.samples.retwisj.replication;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;


import org.springframework.data.redis.samples.retwisj.command.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class BroadcasterGRPC extends Broadcaster {

	private HashSet<BroadcastServiceGrpc.BroadcastServiceBlockingStub> replicas = 
											new HashSet<BroadcastServiceGrpc.BroadcastServiceBlockingStub>();
	
	
	private void log(String hostAddr) {
		try {
			System.out.println("Will add " + hostAddr);
			
			ManagedChannel channel = ManagedChannelBuilder.forAddress(hostAddr, 5051).usePlaintext(true).build();
			
			BroadcastServiceGrpc.BroadcastServiceBlockingStub stub = BroadcastServiceGrpc.newBlockingStub(channel);			
		
			replicas.add(stub);
				
			System.out.println("Added " + hostAddr);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	  
	
	//should return sucess/failure?
	public void broadcast(CommandData data){
		for(BroadcastServiceGrpc.BroadcastServiceBlockingStub stub: replicas){
			BroadcastCommandGrpc cmd = BroadcastCommandGrpc
									.newBuilder()
									.setCmd(CommandData.getIntFromType(data.getCmd()))
									.addAllArguments(data.getArguments())
									.build();
			try {
				stub.send(cmd);
				
			} catch (StatusRuntimeException e) {
				e.printStackTrace();
				return;
			}

		}
	}
	
	public void initialize(){		
		String retAddr = System.getenv("RET_LINKS");
		for(String addr: retAddr.split(":")){
			log(addr);
		}		
	}
}
