package acl;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import acl.command.*;

@Named
public class ACLInterface {
	
	private ACL acl;
	
	@Inject
	public ACLInterface(ACL acl){
		this.acl = acl;
		
	}
	
	public Set<String> blocks(String uid){
		return acl.blocks(uid);
	}
	
	public Set<String> blockedBy(String uid){
		return acl.blockedBy(uid);
	}
	
	public void block(String uid, String targetUid){
		acl.block(uid, targetUid);	
		Broadcaster.broadcast(new BroadcastCommand(CommandType.BLOCK, 
				new ArrayList<String>(Arrays.asList(uid, targetUid))));	
    }
	
	public void unblock(String uid, String targetUid){
		acl.unblock(uid, targetUid);
    	Broadcaster.broadcast(new BroadcastCommand(CommandType.UNBLOCK, 
				new ArrayList<String>(Arrays.asList(uid, targetUid))));	
	}
	
	public ACL getACL(){
		return acl;
	}
}