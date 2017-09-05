package sandbox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@RestController
public class Sandbox {
	List<String> replicas = new ArrayList<String>();
	List<User> users = new ArrayList<User>();
	
	final int n_users = 10;
	final int actions = 50;
	final double block = 0.2;
	final double read = 0.6;
	final double post = 0.2;
	final double delayChance = 0.2;
	final int delay = 20;
	
	private int logs = 0;
	private int logsOfUsers = 0;
	
	public Sandbox(){
		String replicaList = System.getenv("RET_LINKS");
		
		for(String replica: replicaList.split(":")){
			replicas.add(replica);
		}		
	}
	
	@RequestMapping("/replica_log")	
	public String replicaLog(){
		log();
		return "success";
	}
	
	@RequestMapping("/user_log")
	public String userLog(){
		logUser();
		return "success";
	}
	
	public synchronized void log(){
		logs++;
		System.out.println("ONE REPLICA LOGGED!");
		if(logs == replicas.size() ){
			registerUsers();
		}
	}
	
	public synchronized void logUser(){
		logsOfUsers++;

		System.out.println("ONE USER LOGGED!");
		if(logsOfUsers == n_users*replicas.size()){
			runTests();
		}
	}
	
	
	public void registerUsers(){
		
		for(int i = 0; i<n_users; i++){
			System.out.println("Registering user " + i);
			
			List<String> names = new ArrayList<String>();
			for(int j = 0; j<n_users; j++){
				if (i != j)
					names.add("u" + j);					
			}
			
			User u = new User("u" + i, replicas.get((int)(Math.random()*(replicas.size()))), names);
			users.add(u);
			u.signUp();
		}
	}
	
	public void runTests(){
		Set<Thread> threads = new HashSet<Thread>();
		
		for(User u: users){
			Thread thread = new Thread(new UserAction(u));
			threads.add(thread);
			thread.start();
		}
		
		//wait for treads
		for(Thread thread: threads){
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		int reads=0;
		int badReads=0;
		
		for(User user: users){
			System.out.println("LOG FOR USER " + user.name + " in replica " + user.replica + ":\n" +user.getLog()+"\n");
			reads += user.getReads();
			badReads += user.getBadReads();
		}
		
		System.out.println("\n--------SUMMARY---------");
		System.out.println("\n  Of " + reads + " reads, " + badReads + " were bad reads.");
		System.out.println("\n  Failure Rate: " + (double)badReads/reads*100 + "%.");
		
		
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
					if(user.getUnblockedUsers().size()>0){
						if(Math.random()<delayChance)
							user.block(user.getUnblockedUsers().get((int)Math.random()*user.getUnblockedUsers().size()), delay);
						else
							user.block(user.getUnblockedUsers().get((int)Math.random()*user.getUnblockedUsers().size()), 0);
						user.log("-block\n");
					} else {
						user.log("-block tried and failed (already blocked every user)");
					}
				}
				else if(p<block+read){
					user.log("-read\n");
					
					//reads first post from every user
					
					for(String name: user.getBlockedUsers()){
						String post = user.read(name);
						if(post!=null)
							if (post.contains("!" + user.getName() + "!" ))
								user.log("   : "+ name + " made unsafe post\n");
						
					}
					
					for(String name: user.getUnblockedUsers()){
						String post = user.read(name);
						if(post!=null)
							if (post.contains("!" + user.getName() + "!" ))
								user.log("   : "+ name + " made unsafe post\n");
						
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
		private RestTemplate rest;
		private String name;
		private String replica;
		private String log = "";
		private List<String> context;
		private int reads;
		private int badReads;
		
		public User(String name, String replica, List<String> unblockedUsers){
			this.name = name; 
			this.replica = replica;
			this.unblockedUsers = unblockedUsers;

			rest = new RestTemplate();

			
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
						HttpMethod.POST, 
						null,
						String.class);
			
			context = response.getHeaders().get("Set-Cookie");
			System.out.println("All cookies: " + context);
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
			map.add("content", "postContent: " + content);
			map.add("replyTo", "");
			map.add("replyPid", "");
			
			rest.postForObject("http://"+ this.replica + ":8080/retwisj/!" + this.name, map, String.class);
						
		}
		
		public void block(String userToBlock, int delay){
		
			if(!blockedUsers.contains(userToBlock)){
				
				HttpHeaders headers = new HttpHeaders();
				for(String cookie: context){
					headers.add("Cookie", cookie.split(";")[0].trim());
				}				
				
				String url = "http://" + this.replica + ":8080/retwisj/!" + userToBlock + "/block?delay=" + delay;
				
				rest.exchange(url, HttpMethod.GET, new HttpEntity<String>(headers),String.class);
				this.blockedUsers.add(userToBlock);
				this.unblockedUsers.remove(userToBlock);
			}
		}
		/*
		 * reads the first post of an user
		 */
		public String read(String userToRead){
			HttpHeaders headers = new HttpHeaders();
			for(String cookie: context){
				headers.add("Cookie", cookie.split(";")[0].trim());
			}
			reads++;
			
			
			String post = (rest.exchange("http://" + this.replica + ":8080/retwisj/!" + userToRead + "/read", 
					HttpMethod.GET,
					new HttpEntity<String>(headers),
					String.class).getBody());
			
			if(post!=null)
				if (post.contains("!" + getName() + "!" ))
					badReads++;
			
			return post;
			
		}
		
		public int getReads(){
			return reads;
		}
		
		public int getBadReads(){
			return badReads;
		}
	}
}
