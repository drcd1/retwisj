package acl.replication;

import acl.command.*;

public abstract class Broadcaster{
	public abstract void broadcast(CommandData cmd, int delay);
	public abstract void initialize();
}