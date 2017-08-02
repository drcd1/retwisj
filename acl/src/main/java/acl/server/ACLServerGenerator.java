package acl.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import acl.replication.Broadcaster;
import acl.replication.Receiver;

@Component
public class ACLServerGenerator implements CommandLineRunner {
	
	private ACLServerGRPC grpcServer;
	private ACLServerThrift thriftServer;
	
	@Autowired
	private ACLInterface acl;
	
	public void run(String... args) throws Exception {
		
		grpcServer=new ACLServerGRPC(acl);
		thriftServer=new ACLServerThrift(acl);
		Runnable grpc = new Runnable() {
			public void run() {
				try{
					grpcServer.run();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		Runnable thrift = new Runnable() {
			public void run() {
				try{
					thriftServer.run();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		Runnable receive = new Runnable() {
			public void run() {
				Receiver.run(acl.getACL()); //Receiver receives the propagated changes
			}
		};
		
		new Thread(thrift).start();
		new Thread(grpc).start();
		new Thread(receive).start();
		
		acl.initializeBroadcaster();			
		
	}
}
