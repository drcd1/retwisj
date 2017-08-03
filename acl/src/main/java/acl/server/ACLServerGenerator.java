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
import acl.replication.ReceiverGrpc;
import acl.replication.ReceiverThrift;

@Component
public class ACLServerGenerator implements CommandLineRunner {
	
	private ACLServerGRPC grpcServer;
	private ACLServerThrift thriftServer;
	
	@Autowired
	private ACLInterface acl;
	
	public void run(String... args) throws Exception {
		
		grpcServer=new ACLServerGRPC(acl);
		thriftServer=new ACLServerThrift(acl);
		ReceiverThrift.setAcl(acl.getACL());
		ReceiverGrpc.setAcl(acl.getACL());
		
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
		
		Runnable receiveThrift = new Runnable() {
			public void run() {
				ReceiverThrift.run(); //Receiver receives the propagated changes
			}
		};
		
		Runnable receiveGrpc = new Runnable() {
			public void run() {
				try{
					ReceiverGrpc.run(); //Receiver receives the propagated changes
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		};
		
		new Thread(thrift).start();
		new Thread(grpc).start();
		new Thread(receiveThrift).start();
		new Thread(receiveGrpc).start();
		
		acl.initializeBroadcaster();			
		
	}
}
