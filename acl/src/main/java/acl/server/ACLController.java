package acl.server;

import java.io.IOException;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import acl.replication.Debug;

import org.springframework.web.bind.annotation.RequestMethod;


@RestController
public class ACLController {
	
	@Autowired
	private ACLInterface acl;
	
	@RequestMapping(value = "/blocks", method=RequestMethod.GET)
	public Set<String> blocks(@RequestParam("id") String id){
		return acl.blocks(id);
	}
	
	@RequestMapping(value = "/blockedBy", method=RequestMethod.GET)
	public Set<String> blockedBy(@RequestParam("id") String id){
		return acl.blockedBy(id);
	}
	
	@RequestMapping(value = "/block", method=RequestMethod.GET)
	public String block(@RequestParam("id") String uid, @RequestParam("tid") String targetUid){
		acl.block(uid, targetUid);
		return "sucess";
	}
	
	@RequestMapping(value = "/unblock", method=RequestMethod.GET)
	public String unblock(@RequestParam("id") String uid, @RequestParam("tid") String targetUid){
		acl.unblock(uid, targetUid);
		return "sucess";
	}
	
	@RequestMapping("/activateDelay")
	public String activateDelay(){
		Debug.delay = true;
		return "sucess";
	}

}