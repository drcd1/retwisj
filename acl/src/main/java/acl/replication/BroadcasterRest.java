package acl.replication;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import org.springframework.web.client.RestTemplate;

import acl.command.*;

public class BroadcasterRest extends Broadcaster {

	private HashSet<String> replicas = new HashSet<String>();
	private RestTemplate template = new RestTemplate();
	
	void log(String hostAddr) {
		try {
			System.out.println("Will add " + hostAddr);
			replicas.add("http://" + hostAddr + ":8080/acl/replication/");
				
			System.out.println("Added " + hostAddr);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	  
		
	//should return sucess/failure?
	public void broadcast(CommandData data, int delay){
		for(String replica: replicas){
			System.out.println("Going to broadcast...");
			ThreadMethod r = new ThreadMethod(replica, data, delay);
			new Thread(r).start();

			System.out.println("Am broadcasting...");
		}
	}
	
	
	class ThreadMethod implements Runnable{
		ThreadMethod(String r, CommandData d, int delay){
			this.rep = r;
			this.d = d;
			this.delay = delay;
		}
		private String rep;
		private CommandData d;
		
		private int delay;
		
		public void run(){
			if(delay>0){
				try {
					System.out.println("sleeping through replication");
					TimeUnit.SECONDS.sleep(delay);
					System.out.println("slept through replication");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			String url = rep + "receive?cmd="+CommandData.getIntFromType(d.getCmd());
			url+="&args=";
			for(int i = 0; i<d.getArguments().size()-1; i++){
				url+=d.getArguments().get(i) + ",";
			}
			url+=d.getArguments().get(d.getArguments().size()-1);
			
			template.getForObject(url, String.class);
		}
	};
}
