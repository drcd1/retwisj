package acl.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

import acl.command.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


public class ACLServerThrift{

	public static AclHandler handler;

	public static AclService.Processor<AclService.Iface> processor;
	
	private ACLInterface acl;
	
	public ACLServerThrift(ACLInterface acl) {
		this.acl = acl;
	}
	
	
	public void run() throws Exception {
		handler = new AclHandler(acl);
		processor = new AclService.Processor<AclService.Iface>(handler);

		TServerTransport serverTransport = new TServerSocket(9090);
		TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));
		
		System.out.println("Starting thrift server...");
		server.serve();
		
	}
	


	
	static class AclHandler implements AclService.Iface {
		
		String id = UUID.randomUUID().toString();
		
		private ACLInterface acl;
		
		public AclHandler(ACLInterface acl){
			this.acl = acl;
		}
		
	    @Override
	    public void block(String uid, String targetUid, int delay) {
	    	acl.block(uid, targetUid, delay); 
	    }
	    
	    @Override
	    public void unblock(String uid, String targetUid) {
	    	acl.unblock(uid, targetUid);
	    }
	    
	    @Override
	    public Set<String> blocks(String uid) {
	    	Set<String> blocks = acl.blocks(uid);
	    	return blocks;
	    }
	    
	    @Override
	    public Set<String> blockedBy(String uid) {
	    	System.out.println("Returning blocked by...");
	    	Set<String> blocks = acl.blockedBy(uid);
	    	System.out.println("Returning blocked by2...");
	    	return blocks;	    	
	    }

	}
}
