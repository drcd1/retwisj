package org.springframework.data.redis.samples.retwisj;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.samples.retwisj.redis.RetwisRepository;
import org.springframework.stereotype.Component;

@Component
public class Starter{
		
	@Autowired
	private RetwisRepository retwis;
	
	@PostConstruct
	public void run() throws Exception {
		System.out.println("starting...");
		Runnable broadcast = new Runnable() {
			public void run() {
				Broadcaster.run(); //Broadcaster receives information about other replicas
			}
		};
		
		Runnable receive = new Runnable() {
			public void run() {
				Receiver.run(retwis); //Receiver receives the propagated changes
			}
		};
		
		new Thread(broadcast).start();
		new Thread(receive).start();
	}
}

