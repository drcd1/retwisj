package org.springframework.data.redis.samples.retwisj.remote;

import java.util.Set;

import acl.*;

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

public class ACLInterfaceGRPC implements ACLInterface {
	
	private final ManagedChannel channel;
	private final ACLServiceGrpc.ACLServiceBlockingStub blockingStub;
	
	
	public ACLInterfaceGRPC(){
		 this(ManagedChannelBuilder.forAddress("acl-grpc", 8084).usePlaintext(true).build());
	}
	
	ACLInterfaceGRPC(ManagedChannel channel) {
	    this.channel = channel;
	    blockingStub = ACLServiceGrpc.newBlockingStub(channel);
	}
	
	
	public void shutdown() throws InterruptedException {
	    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	}
	
	@Override
	public void block(String uid, String targetUid) {
		Uids request = Uids.newBuilder().setId(uid).setTargetId(targetUid).build();
		Empty response;
		try {
			response = blockingStub.block(request);
		} catch (StatusRuntimeException e) {
			e.printStackTrace();
			return;
		}

	}

	@Override
	public void unblock(String uid, String targetUid) {
		Uids request = Uids.newBuilder().setId(uid).setTargetId(targetUid).build();
		Empty response;
		try {
			response = blockingStub.unblock(request);
		} catch (StatusRuntimeException e) {
			e.printStackTrace();
			return;
		}

	}

	@Override
	public Set<String> blocks(String uid) {
		Uid request = Uid.newBuilder().setId(uid).build();
		UidSet response;
		try {
			response = blockingStub.blocks(request);
		} catch (StatusRuntimeException e) {
			e.printStackTrace();
			return new HashSet<String>();
		}
		Set<String> tmp = new HashSet<String>(response.getIdsList());
		
		return tmp;
	}

	@Override
	public Set<String> blockedBy(String uid) {
		if(uid==null)
			return new HashSet<String>();
		Uid request = Uid.newBuilder().setId(uid).build();
		UidSet response;
		try {
			response = blockingStub.blockedBy(request);
		} catch (StatusRuntimeException e) {
			e.printStackTrace();
			return new HashSet<String>();
		}
		Set<String> tmp = new HashSet<String>(response.getIdsList());
		
		return tmp;
	}

}
