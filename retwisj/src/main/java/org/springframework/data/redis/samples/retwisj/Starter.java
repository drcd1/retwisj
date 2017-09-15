package org.springframework.data.redis.samples.retwisj;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.samples.retwisj.redis.RetwisRepository;
import org.springframework.data.redis.samples.retwisj.redis.RetwisRepositoryInterface;
import org.springframework.data.redis.samples.retwisj.replication.Broadcaster;
import org.springframework.data.redis.samples.retwisj.replication.ReceiverGrpc;
import org.springframework.data.redis.samples.retwisj.replication.ReceiverThrift;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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
		
		System.out.println("Initialize broadcast...");
		
		retwis.initializeBroadcaster();
		
		RestTemplate rt = new RestTemplate();
		
		while(rt.getForObject("http://acl:8080/acl/ready",String.class).equals("no")){
			System.out.println("Waiting for acl...");
			TimeUnit.SECONDS.sleep(15);
		}
		
		if(System.getenv("RUN_TESTS").equals("1")){
			System.out.println("logging replica...");
			Thread t = new Thread(new Runnable(){
				public void run(){
					RestTemplate rt = new RestTemplate();
					rt.getForObject("http://sandbox:8080/sandbox/replica_log", String.class);
				
				}
			});
		
			t.start();
			System.out.println("logged replica...");
		}
		
	}
}

