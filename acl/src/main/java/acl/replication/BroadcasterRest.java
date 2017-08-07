package acl.replication;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import org.springframework.web.client.RestTemplate;

import acl.command.*;

public class BroadcasterRest extends Broadcaster {

	private HashSet<String> replicas = new HashSet<String>();
	private RestTemplate template = new RestTemplate();
	
	private void log(String hostAddr) {
		try {
			System.out.println("Will add " + hostAddr);
			replicas.add("http://" + hostAddr + ":8080/acl/replication/");
				
			System.out.println("Added " + hostAddr);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	  
		
	//should return sucess/failure?
	public void broadcast(CommandData data){
		for(String replica: replicas){
			ThreadMethod r = new ThreadMethod(replica, data);
			new Thread(r).start();
		}
		
		Debug.delay = false;
	}
	
	public void initialize(){
		String retAddr = System.getenv("ACL_LINKS");
		for(String addr: retAddr.split(":")){
			log(addr);
		}
	}
	
	class ThreadMethod implements Runnable{
		ThreadMethod(String r, CommandData d){
			this.rep = r;
			this.d = d;
		}
		private String rep;
		private CommandData d;
		
		private boolean delay = Debug.delay;
		
		public void run(){
			if(delay){
				try {
					TimeUnit.SECONDS.sleep(60);
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
