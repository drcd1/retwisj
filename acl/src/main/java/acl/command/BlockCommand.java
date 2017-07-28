package acl.command;

import java.util.List;

import acl.ACL;

public class BlockCommand extends Command {
	private String id;
	private String targetId;
	
	public BlockCommand(List<String> args){
		id = args.get(0);
		targetId = args.get(1);
	}
	
	
	@Override
	public void run(ACL acl) {
		acl.block(id, targetId);
	}

}
