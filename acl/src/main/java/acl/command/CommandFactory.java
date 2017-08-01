package acl.command;


public class CommandFactory {
	
	public static Command get(BroadcastCommand cmd) throws Exception{
		switch(cmd.getCmd()){
			case BLOCK:
				return new BlockCommand(cmd.getArguments());
			case UNBLOCK:
				return new UnblockCommand(cmd.getArguments());
			default:
				throw new Exception(); //should not happen
		}
	}
}
