package org.springframework.data.redis.samples.retwisj.command;

import java.util.List;

import org.springframework.data.redis.samples.retwisj.redis.RetwisRepository;

public class FollowCommand extends Command {
	
	String uid;
	String targetUser;
	
	public FollowCommand(List<String> args){
		uid = args.get(0);
		targetUser = args.get(1);		
	}

	@Override
	public void run(RetwisRepository retwis) {
		System.out.println("Following: " + uid + " follows " + targetUser);
		retwis.follow(uid, targetUser);
	}

}
