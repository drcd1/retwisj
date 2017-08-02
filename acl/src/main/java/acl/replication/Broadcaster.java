package acl.replication;

import acl.command.*;

public abstract class Broadcaster{
	public abstract void broadcast(CommandData cmd);
	public abstract void initialize();
}