package acl.server;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import acl.ACL;
import acl.command.*;
import acl.replication.Broadcaster;
import acl.replication.BroadcasterGRPC;
import acl.replication.BroadcasterThrift;

import acl.replication.BroadcasterRest;

@Named
public class ACLInterface {
	
	private ACL acl;
	private Broadcaster broadcaster;
	
	private boolean ready = false;
	
	@Inject
	public ACLInterface(ACL acl){
		this.acl = acl;
		broadcaster = new BroadcasterGRPC();
	}
	
	public Set<String> blocks(String uid){
		return acl.blocks(uid);
	}
	
	public Set<String> blockedBy(String uid){
		return acl.blockedBy(uid);
	}
	
	public void block(String uid, String targetUid, int delay){
		acl.block(uid, targetUid);	
		broadcaster.broadcast(new CommandData(CommandData.Type.BLOCK, 
				new ArrayList<String>(Arrays.asList(uid, targetUid))), delay);	
    }
	
	public void unblock(String uid, String targetUid){
		acl.unblock(uid, targetUid);
    	broadcaster.broadcast(new CommandData(CommandData.Type.UNBLOCK, 
				new ArrayList<String>(Arrays.asList(uid, targetUid))), 0);	
	}
	
	public ACL getACL(){
		return acl;
	}
	
	public void initializeBroadcaster(){
		broadcaster.initialize();
	}
	
	public boolean isReady(){
		return ready;
	}
	
	public void ready(){
		ready = true;
	}
	
}