package org.springframework.data.redis.samples.retwisj.replication;

import org.springframework.data.redis.samples.retwisj.command.*;

public abstract class Broadcaster{
	public abstract void broadcast(CommandData cmd);
	
	abstract void log(String addr);
	

	public void initialize(){
		String retAddr = System.getenv("RET_LINKS");
		String myName = System.getenv("MY_NAME");
		for(String addr: retAddr.split(":")){
			if(!addr.equals(myName))
				log(addr);
		}		
	}
}