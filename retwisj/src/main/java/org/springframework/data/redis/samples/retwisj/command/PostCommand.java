package org.springframework.data.redis.samples.retwisj.command;

import java.util.List;

import org.springframework.data.redis.samples.retwisj.Post;
import org.springframework.data.redis.samples.retwisj.redis.RetwisRepository;

public class PostCommand extends Command {
	
	String name;
	Post post;
	String pid;
	String replyName;
	
	public PostCommand(List<String> args){
		name = args.get(0);
		post = new Post(	args.get(1), 
							args.get(2),
							args.get(3),
							args.get(4),
							args.get(5));
		pid = args.get(6);
		replyName = args.get(7);
		
	}

	@Override
	public void run(RetwisRepository retwis) {
		retwis.post(name, post, pid, replyName);
	}

}
