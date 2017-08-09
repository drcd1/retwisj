package org.springframework.data.redis.samples.retwisj.remote;

import java.util.HashSet;
import java.util.Set;

import org.springframework.web.client.RestTemplate;

public class ACLInterfaceRest implements ACLInterface{
	
	private final RestTemplate rest = new RestTemplate();
	
	public void block(String uid, String targetUid, int delay){
		rest.getForObject("http://acl:8080/acl/block?id="+uid + "&tid=" + targetUid + "&delay=" + delay, String.class);
	}

	public void unblock(String uid, String targetUid){
		rest.getForObject("http://acl:8080/acl/unblock?id="+uid + "&tid=" + targetUid, String.class);
	}
	
	public Set<String> blocks(String uid){
		return rest.getForObject("http://acl:8080/acl/blocks?id="+uid, Set.class);
	}
	
	public Set<String> blockedBy(String uid){
		if(uid == null)
			return new HashSet<String>();
		return rest.getForObject("http://acl:8080/acl/blockedBy?id="+uid, Set.class);
	}
	
	
}
