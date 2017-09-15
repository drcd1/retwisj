package sandbox;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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
	
	private int n_users = 10;
	private int n_actions = 50;
	private double block = 0.2;
	private double read = 0.8;
	private double post = 0.2;
	private double delayChance = 0.2;
	private int delay = 20;
	
	private int logs = 0;
	private int logsOfUsers = 0;
	
	public Sandbox(){
		String replicaList = System.getenv("RET_LINKS");		
		for(String replica: replicaList.split(":")){
			replicas.add(replica);
		}		
		
		
	
		try {
			BufferedReader br = new BufferedReader(new FileReader("src/main/resources/sandbox.properties"));
			for(String line; (line = br.readLine()) != null;){
				
				String[] parsed = line.split(":");
				if(parsed[0] != null && parsed[1] != null){
					String key = parsed[0].trim();
					
					if(key.equals("n_users")){
						n_users = Integer.parseInt(parsed[1].trim());
					} else if (key.equals("n_actions")){
						n_actions = Integer.parseInt(parsed[1].trim());
					} else if (key.equals("block")){
						block = Double.parseDouble(parsed[1].trim());
					} else if (key.equals("read")){
						read = Double.parseDouble(parsed[1].trim());
					} else if (key.equals("post")){
						post = Double.parseDouble(parsed[1].trim());
					} else if (key.equals("delayChance")){
						delayChance = Double.parseDouble(parsed[1].trim());
					} else if (key.equals("delay")){
						delay = Integer.parseInt(parsed[1].trim());
					}
				}
				
			}
			br.close();
		} catch (FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Running tests with the following parameters:");
		System.out.println("-n_users:     " + n_users);
		System.out.println("-n_actions:   " + n_actions);
		System.out.println("-block:       " + block);
		System.out.println("-read:        " + read);
		System.out.println("-post:        " + post);
		System.out.println("-delayChance: " + delayChance);
		System.out.println("-delay:       " + delay);
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
		if(logs == replicas.size() ){
			registerUsers();
		}
	}
	
	public synchronized void logUser(){
		logsOfUsers++;

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
		GlobalData globalData = new GlobalData();
		for(User u: users){
			Thread thread = new Thread(new UserAction(u, globalData));
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
		System.out.println("\nOf " + reads + " reads, " + badReads + " were bad reads.");
		System.out.println("Failure Rate: " + (double)badReads/reads*100 + " %.");
		
		
	}
	
	public class GlobalData {
		private Map<String, Set<UserData>> data = new ConcurrentHashMap<String, Set<UserData>>();
		
		public GlobalData(){
			for(User user: users){
				data.put(user.getName(), new HashSet<UserData>());
			}
		}
		
		public synchronized Set<UserData> getBlockers(String name){
			Set<UserData> tmp = new HashSet<UserData>();
			tmp.addAll(data.get(name));
			return tmp;
		}
		
		public synchronized void setBlocker(String blocked, UserData blocker){
			Set<UserData> updated = data.get(blocked);
			updated.add(blocker);
			data.put(blocked, updated);
		}
	}
	
	public class UserData{
		private String name;
		private String replica;
		
		public UserData(String n, String r){
			name = n;
			replica = r;
		}
		
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getReplica() {
			return replica;
		}
		public void setReplica(String replica) {
			this.replica = replica;
		}
		
	
	}
	
	public class UserAction implements Runnable {
		User user;
		GlobalData globalData;
		
		public UserAction(User u, GlobalData gd){
			user = u;
			globalData = gd;
		}
		
		@Override
		public void run(){
			user.signIn();
			for(int i = 0; i<n_actions; i++){
				double p = Math.random();
				int randomIndex = -1;
				String nameToBlock = "";
				if(p<block){
					if(user.getUnblockedUsers().size()>0){
						randomIndex = (int)Math.random()*user.getUnblockedUsers().size();
						nameToBlock = user.getUnblockedUsers().get(randomIndex);
						if(Math.random()<delayChance)
							user.block(nameToBlock, delay);
						else
							user.block(nameToBlock, 0);
						user.log("-block " + nameToBlock + " at time " + Instant.now() + "\n");
					} else {
						user.log("-block tried and failed (already blocked every user)\n");
					}
					
					user.post();
					if(randomIndex>=0)
						globalData.setBlocker(nameToBlock, new UserData(user.getName(),user.getReplica()));
				}
				else {
					user.log("-read\n");
					
					//reads first post from every that has blocked him
					Set<UserData> bl = globalData.getBlockers(user.getName());
					for(UserData ud: bl){
						if(!ud.getReplica().equals(user.getReplica())){
							user.log("   reading " + ud.getName() + " at time " + Instant.now() + "\n");
							String post = user.read(ud.getName());
							if(post!=null)
								if (post.contains("!" + user.getName() + "!" ))
									user.log("   : "+ ud.getName() + " made unsafe post\n");
						}
					}
					
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
		
		public String getReplica() {
			return replica;
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
			rest.postForObject("http://"+ System.getenv("RET_LINKS").split(":")[0]+ ":8080/retwisj/signUp", map, String.class);	
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
