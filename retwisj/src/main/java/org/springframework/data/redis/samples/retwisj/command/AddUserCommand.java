package org.springframework.data.redis.samples.retwisj.command;

import java.util.List;

import org.springframework.data.redis.samples.retwisj.redis.RetwisRepository;

public class AddUserCommand extends Command {
	
	String name;
	String pass;
	
	public AddUserCommand(List<String> args){
		name = args.get(0);
		pass = args.get(1);		
	}

	@Override
	public void run(RetwisRepository retwis) {
		System.out.println("Adding user: " + name);
		retwis.addUser(name, pass);
	}

}
