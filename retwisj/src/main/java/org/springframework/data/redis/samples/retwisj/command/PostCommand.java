package org.springframework.data.redis.samples.retwisj.command;

import java.util.List;

import org.springframework.data.redis.samples.retwisj.redis.RetwisRepository;
import org.springframework.data.redis.samples.retwisj.web.WebPost;

public class PostCommand extends Command {
	
	String name;
	WebPost post;
	
	public PostCommand(List<String> args){
		name = args.get(0);
		post = new WebPost(	args.get(1), 
							args.get(2),
							args.get(3),
							args.get(4),
							args.get(5),
							args.get(6),
							args.get(7));
		
	}

	@Override
	public void run(RetwisRepository retwis) {
		System.out.println("Posting: " + name + " posts the following: ");
		System.out.println("  content: " + post.getContent());
		System.out.println("  name:    " + post.getName()) ;
		System.out.println("  Reply to:"+post.getReplyTo());
		System.out.println("  ReplyPid:"+post.getReplyPid()); 
		System.out.println("  pid:     "+post.getPid());
		System.out.println("  time:    "+post.getTime());
		System.out.println("  timeArg: " +post.getTimeArg());
		
		
		retwis.post(name, post);
	}

}
