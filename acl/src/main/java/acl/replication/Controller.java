package acl.replication;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import acl.ACL;
import acl.command.Command;
import acl.command.CommandData;
import acl.command.CommandFactory;

import org.springframework.web.bind.annotation.RequestMethod;


@RestController
@RequestMapping("/replication")
public class Controller {
	
	@Autowired
	private ACL acl;
	
	public void setAcl(ACL acl){
		this.acl = acl;
	}
	
	@RequestMapping(value = "receive", method=RequestMethod.GET)
	public String receive(@RequestParam("cmd") String cmd, @RequestParam("args") List<String> args){
		try {
			acl.execute(CommandFactory.get(new CommandData(CommandData.getTypeFromInt(Integer.parseInt(cmd)), args)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "sucess";
	}

}