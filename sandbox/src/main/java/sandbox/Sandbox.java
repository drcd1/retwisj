package sandbox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
public class Sandbox implements CommandLineRunner {
	List<String> replicas = new ArrayList<String>();
	List<User> users = new ArrayList<User>()	;
	
	final int n_users = 50;
	final int actions = 10;
	final double block = 0.1;
	final double read = 0.8;
	final double post = 0.1;
	
	public void run(String... args){
		System.out.println("Running...");

		String replicaList = System.getenv("RET_LINKS");
		
		Set<Thread> threads = new HashSet<Thread>();
		
		for(String replica: replicaList.split(":")){
			replicas.add(replica);
		}
		
		

		System.out.println("Running2...");
		
		for(int i = 0; i<n_users; i++){
			List<String> names = new ArrayList<String>();
			for(int j = 0; j<n_users; j++){
				if (i != j)
					names.add("u" + j);					
			}
			
			User u = new User("u" + i, replicas.get((int)(Math.random()*(replicas.size()))), names);
			users.add(u);
			u.signUp();
		}
		try {
			TimeUnit.SECONDS.sleep(30);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println("Running3...");
		
		for(User u: users){
			Thread thread = new Thread(new UserAction(u));
			threads.add(thread);
			thread.start();
		}
		
		System.out.println("Running4...");
		
		for(Thread thread: threads){
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Running5...");
		
		for(User user: users){
			System.out.println("LOG FOR USER " + user.name + ":\n" +user.getLog());
		}
		
		
	
		
	}
	
	public class UserAction implements Runnable {
		User user;
		
		public UserAction(User u){
			user = u;
		}
		
		@Override
		public void run(){
			user.signIn();
			for(int i = 0; i<actions; i++){
				double p = Math.random();
				if(p<block){
					user.block(user.getUnblockedUsers().get((int)Math.random()*user.getUnblockedUsers().size()), 0);
					user.log("-block\n");
				}
				else if(p<block+read){
					user.log("-read\n");
					for(String name: user.getBlockedUsers()){
						String post = user.read(name);
						if(post!=null)
							if (post.contains("!" + name + "!" ))
								user.log(": "+ name + " made unsafe post\n");
						
					}
					
					for(String name: user.getUnblockedUsers()){
						String post = user.read(name);
						if(post!=null)
							if (post.contains("!" + name + "!" ))
								user.log(": "+ name + " made unsafe post\n");
						
					}
					
				}
				else{
					user.post();
					user.log("-post\n");
				}
			}
		}
	}
	
	public class User{
		private List<String> unblockedUsers;
		private List<String> blockedUsers = new ArrayList<String>();
		private RestTemplate rest = new RestTemplate();
		private String name;
		private String replica;
		private String log = "";
		private List<String> context;
		
		public User(String name, String replica, List<String> unblockedUsers){
			this.name = name; 
			this.replica = replica;
			this.unblockedUsers = unblockedUsers;
		}
		
		public List<String> getUnblockedUsers(){
			return unblockedUsers;
		}
		
		public List<String> getBlockedUsers(){
			return blockedUsers;
		}
		
		public String getName(){
			return name;
		}
		
		public void log(String str){
			this.log+=str;
		}
		
		public String getLog(){
			return log;
		}
		
		public void signIn(){
			
			HttpEntity<String> response = rest.exchange("http://"+ this.replica + ":8080/retwisj/signIn?name="+name+"&pass="+name,
						HttpMethod.GET, 
						null,
						String.class);
			
			context = response.getHeaders().get("Set-Cookie");
		}
		
		public void signUp(){
			MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
			map.add("name",  this.name);
			map.add("pass",  this.name);
			map.add("pass2", this.name);
			rest.postForObject("http://"+ this.replica + ":8080/retwisj/signUp", map, String.class);	
		}
		
		public void post(){
			String content ="";
			for(String name: blockedUsers){
				content+= "!" +  name + "!";
			}
			
			MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
			map.add("content", content);
			map.add("replyTo", "");
			map.add("replyPid", "");
			
			rest.postForObject("http://"+ this.replica + ":8080/retwisj/!" + this.name, map, String.class);
						
		}
		
		public void block(String userToBlock, int delay){
		
			if(!blockedUsers.contains(userToBlock)){
				rest.getForObject("http://" + this.replica + ":8080/retwisj/!" + userToBlock + "/block?delay=" + delay + "&blockedBy=" + this.name,
							 	String.class);
				this.blockedUsers.add(userToBlock);
				this.unblockedUsers.remove(userToBlock);
			}
		}
		
		public String read(String userToRead){
			HttpHeaders headers = new HttpHeaders();
			for(String cookie: context)
				headers.add("Cookie", cookie);
			/* Is not working!
			 * 
			 * 
			System.out.println(rest.exchange("http://" + this.replica + ":8080/retwisj/!" + userToRead + "/read", 
					HttpMethod.GET,
					new HttpEntity<String>(headers),
					String.class).getBody());
			*/
			
			return "aa";
		}
		
	}
}
