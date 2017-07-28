package acl.command;

import acl.ACL;

public abstract class Command {
	public enum Type{
		BLOCK, UNBLOCK
	}
	
	public abstract void run(ACL acl);
	
}
