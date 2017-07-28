package acl.command;

import java.util.List;

import acl.ACL;

public class UnblockCommand extends Command {
	private String id;
	private String targetId;
	
	public UnblockCommand(List<String> args){
		id = args.get(0);
		targetId = args.get(1);
	}
	
	
	@Override
	public void run(ACL acl) {
		acl.unblock(id, targetId);
	}

}
