package acl.command;


public class CommandFactory {
	
	public static Command get(CommandData cmd) throws Exception{
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
