package org.springframework.data.redis.samples.retwisj.command;

import org.springframework.data.redis.samples.retwisj.redis.RetwisRepository;

public abstract class Command {	
	public abstract void run(RetwisRepository retwis);
	
}
