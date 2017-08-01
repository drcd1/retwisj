package org.springframework.data.redis.samples.retwisj.command;

import java.util.List;

import org.springframework.data.redis.samples.retwisj.redis.RetwisRepository;

public class AddAuthCommand extends Command {
	
	String name;
	
	public AddAuthCommand(List<String> args){
		name = args.get(0);
	}

	@Override
	public void run(RetwisRepository retwis) {
		System.out.println("Adding auth: " + name);
		retwis.addAuth(name);
	}

}
