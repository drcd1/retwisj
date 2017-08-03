package org.springframework.data.redis.samples.retwisj.replication;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.samples.retwisj.command.CommandData;
import org.springframework.data.redis.samples.retwisj.command.CommandFactory;
import org.springframework.data.redis.samples.retwisj.redis.RetwisRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/replication")
public class BroadcastController {
	
	@Autowired
	private RetwisRepository retwis;
	
	
	@RequestMapping(value = "receive", method=RequestMethod.GET)
	@ResponseBody
	public String receive(@RequestParam("cmd") String cmd, @RequestParam("args") List<String> args){
		try {
			retwis.execute(CommandFactory.get(new CommandData(CommandData.getTypeFromInt(Integer.parseInt(cmd)), args)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "sucess";
	}

}