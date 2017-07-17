package acl;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

@Named
public class ACL {
	
	private SetOperations<String, String> setOps;
	
	private final StringRedisTemplate template;
	

	
	
	@Inject
	public ACL(StringRedisTemplate template){
		this.template = template;
		setOps = this.template.opsForSet();
		
	}
	
	Set<String> blocks(String uid){
		return setOps.members(uid + ":block");
	}
	
	Set<String> blockedBy(String uid){
		return setOps.members(uid + ":blockedBy");
	}
	
	void block(String uid, String targetUid){
		setOps.add(uid + ":block", targetUid);
		setOps.add(targetUid + ":blockedBy", uid);
		
		System.out.println(uid + " block " + targetUid);
		
	}
	void unblock(String uid, String targetUid){
		
		setOps.remove(uid + ":block", targetUid);
		setOps.remove(targetUid + ":blockedBy", uid);
		
		System.out.println(uid + " unblock " + targetUid);		
	}
}