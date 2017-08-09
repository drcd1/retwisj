package org.springframework.data.redis.samples.retwisj.remote;

import java.util.Set;

import acl.*;

import java.net.*;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.concurrent.TimeUnit;


import com.google.common.annotations.VisibleForTesting;
import com.google.protobuf.Message;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ACLInterfaceThrift implements ACLInterface {
	
	private TTransport transport;
	
	private TProtocol protocol;
	
	private AclService.Client client;
	
	private boolean open = false;
	
	public ACLInterfaceThrift(){
		try {
			transport = new TSocket("acl", 9090);
			
			
		 } catch (Exception x) {
		      x.printStackTrace();
		 }
	}
	
	public boolean openTransport(){
		try{
			transport.open();
		} catch(Exception e){
			return false;
		}
		return true;
	}
	
	public void start(){
		while (!openTransport()){
			try{
				TimeUnit.SECONDS.sleep(10);
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	  

		protocol = new  TBinaryProtocol(transport);
		client = new AclService.Client(protocol);
      
     	open = true;
     	
      
	}
	
	@Override
	public void block(String uid, String targetUid, int delay) {
		if(!open){
			start();
		}
		try {
			client.block(uid, targetUid, delay);
		} catch (TException x) {
		      x.printStackTrace();
		}

	}

	@Override
	public void unblock(String uid, String targetUid) {
		if(!open){
			start();
		}
		try {
			client.unblock(uid, targetUid);
		} catch (TException x) {
		      x.printStackTrace();
		}

	}

	@Override
	public Set<String> blocks(String uid) {
		if(!open){
			start();
		}
		try {
			Set<String> tmp = client.blocks(uid);			
			return tmp;
		}catch (TException x) {
		      x.printStackTrace();
			  return new HashSet<String>();
		}
		
	}

	@Override
	public Set<String> blockedBy(String uid) {
		if(uid==null)
			return new HashSet<String>();
		if(!open){
			start();
		}
		try{
			Set<String> tmp = client.blockedBy(uid);			
			return tmp;
		} catch (TException x) {
		      x.printStackTrace();
			  return new HashSet<String>();
		}
		
		
	}

}


