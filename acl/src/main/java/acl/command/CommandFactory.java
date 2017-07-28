package acl.command;

import acl.*;

public class CommandFactory {
	
	public static Command get(BroadcastCommand cmd) throws Exception{
		int block = Command.Type.BLOCK.ordinal();
		int unblock = Command.Type.UNBLOCK.ordinal();
		if(cmd.getCmd()==block){
			return new BlockCommand(cmd.getArguments());
		}
		else if(cmd.getCmd()==unblock){
			return new UnblockCommand(cmd.getArguments());
		}
		else {
			throw new Exception(); //should not happen
		}
	}
}
