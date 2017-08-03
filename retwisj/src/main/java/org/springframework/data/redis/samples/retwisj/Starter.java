package org.springframework.data.redis.samples.retwisj;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.samples.retwisj.redis.RetwisRepository;
import org.springframework.data.redis.samples.retwisj.redis.RetwisRepositoryInterface;
import org.springframework.data.redis.samples.retwisj.replication.Broadcaster;
import org.springframework.data.redis.samples.retwisj.replication.ReceiverGrpc;
import org.springframework.data.redis.samples.retwisj.replication.ReceiverThrift;
import org.springframework.stereotype.Component;

@Component
public class Starter{

	
	@Autowired
	private RetwisRepositoryInterface retwis;
	
	@PostConstruct
	public void run() throws Exception {
		System.out.println("starting...");
		
		ReceiverThrift.setRetwis(retwis.getRetwis());
		ReceiverGrpc.setRetwis(retwis.getRetwis());
				
		Runnable receiveThrift = new Runnable() {
			public void run() {
				ReceiverThrift.run(); //Receiver receives the propagated changes
			}
		};
		
		Runnable receiveGrpc = new Runnable() {
			public void run() {
				try{
					ReceiverGrpc.run(); //Receiver receives the propagated changes
				} catch (Exception e){
					e.printStackTrace();
				}
			}	
		};
		
		new Thread(receiveThrift).start();
		new Thread(receiveGrpc).start();
		
		retwis.initializeBroadcaster();
	}
}

