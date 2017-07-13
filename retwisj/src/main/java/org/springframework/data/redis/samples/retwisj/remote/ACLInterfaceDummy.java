package org.springframework.data.redis.samples.retwisj.remote;

import java.util.HashSet;
import java.util.Set;

public class ACLInterfaceDummy implements ACLInterface {
	public void block(String uid, String targetUid){
		//does nothing
	}

	public void unblock(String uid, String targetUid){
		//does nothing
	}
	
	public Set<String> blocks(String uid){
		Set<String> tmp = new HashSet<String>();
		tmp.add("2");
		tmp.add("3");
		
		return tmp;
	}
	
	public void nothing(){}
}
