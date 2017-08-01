package org.springframework.data.redis.samples.retwisj.command;

import java.util.List;

import org.springframework.data.redis.samples.retwisj.redis.RetwisRepository;

public class DeleteAuthCommand extends Command {
	
	String user;
	
	public DeleteAuthCommand(List<String> args){
		user = args.get(0);	
	}

	@Override
	public void run(RetwisRepository retwis) {
		System.out.println("Deleting auth: " + user);
		retwis.deleteAuth(user);
	}

}
