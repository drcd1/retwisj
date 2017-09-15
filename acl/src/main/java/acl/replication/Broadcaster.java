package acl.replication;

import acl.command.*;

public abstract class Broadcaster{
	public abstract void broadcast(CommandData cmd, int delay);
	public void initialize(){
		String retAddr = System.getenv("ACL_LINKS");
		String myName = System.getenv("MY_NAME");
		for(String addr: retAddr.split(":")){
			if(!addr.equals(myName))
				log(addr);
		}		
	}
	
	abstract void log(String addr);
}