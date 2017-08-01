package org.springframework.data.redis.samples.retwisj.command;

import java.util.List;

import org.springframework.data.redis.samples.retwisj.redis.RetwisRepository;

public class StopFollowingCommand extends Command {
	
	String uid;
	String targetUser;
	
	public StopFollowingCommand(List<String> args){
		uid = args.get(0);
		targetUser = args.get(1);			
	}

	@Override
	public void run(RetwisRepository retwis) {
		System.out.println("Stopped following: " + uid + " doesn't follow " + targetUser);
		retwis.stopFollowing(uid, targetUser);
	}

}
