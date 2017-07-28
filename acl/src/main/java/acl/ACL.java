package acl;


import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import acl.command.Command;



@Named
public class ACL {
	
	private SetOperations<String, String> setOps;
	
	private final StringRedisTemplate template;	
	
	@Inject
	public ACL(StringRedisTemplate template){
		this.template = template;
		setOps = this.template.opsForSet();
		
	}
	
	public Set<String> blocks(String uid){
		return setOps.members(uid + ":block");
	}
	
	public Set<String> blockedBy(String uid){
		return setOps.members(uid + ":blockedBy");
	}
	
	public void block(String uid, String targetUid){
		setOps.add(uid + ":block", targetUid);
		setOps.add(targetUid + ":blockedBy", uid);
	}
	public void unblock(String uid, String targetUid){
		setOps.remove(uid + ":block", targetUid);
		setOps.remove(targetUid + ":blockedBy", uid);
	}
	
	public void execute(Command cmd){
		cmd.run(this);
	}
}