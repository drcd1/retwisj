package acl.replication;

import java.util.HashSet;
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
			String url = replica + "receive?cmd="+CommandData.getIntFromType(data.getCmd());
			url+="&args=";
			for(int i = 0; i<data.getArguments().size()-1; i++){
				url+=data.getArguments().get(i) + ",";
			}
			url+=data.getArguments().get(data.getArguments().size()-1);
			
			
			template.getForObject(url, String.class);
		}
	}
	
	public void initialize(){
		String retAddr = System.getenv("ACL_LINKS");
		for(String addr: retAddr.split(":")){
			log(addr);
		}
	}
}
