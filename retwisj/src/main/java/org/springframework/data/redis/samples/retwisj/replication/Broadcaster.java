package org.springframework.data.redis.samples.retwisj.replication;

import org.springframework.data.redis.samples.retwisj.command.*;

public abstract class Broadcaster{
	public abstract void broadcast(CommandData cmd);
	public abstract void initialize();
}