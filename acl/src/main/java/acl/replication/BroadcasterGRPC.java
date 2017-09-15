package acl.replication;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import acl.command.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class BroadcasterGRPC extends Broadcaster {

	private HashSet<BroadcastServiceGrpc.BroadcastServiceBlockingStub> replicas = 
											new HashSet<BroadcastServiceGrpc.BroadcastServiceBlockingStub>();
	
	
	void log(String hostAddr) {
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
	public void broadcast(CommandData data, int delay){
		BroadcastCommandGrpc cmd = BroadcastCommandGrpc
				.newBuilder()
				.setCmd(CommandData.getIntFromType(data.getCmd()))
				.addAllArguments(data.getArguments())
				.build();
		for(BroadcastServiceGrpc.BroadcastServiceBlockingStub stub: replicas){
			ThreadMethod r = new ThreadMethod(stub, cmd, delay);
			new Thread(r).start();
		}
	}
	
	class ThreadMethod implements Runnable{
		ThreadMethod(BroadcastServiceGrpc.BroadcastServiceBlockingStub stub, BroadcastCommandGrpc cmd, int delay){
			this.stub = stub;
			this.cmd = cmd;
			this.delay = delay;
		}
		
		private BroadcastServiceGrpc.BroadcastServiceBlockingStub stub;
		private BroadcastCommandGrpc cmd;
		private int delay;
		
		public void run(){
			if(delay>0){
				try {
					TimeUnit.SECONDS.sleep(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			try {
				stub.send(cmd);
				
			} catch (StatusRuntimeException e) {
				e.printStackTrace();
				return;
			}
		}
	};
}
