package org.springframework.data.redis.samples.retwisj.command;



public class CommandFactory {
		
	public static Command get(CommandData cmd) throws Exception{
		switch(cmd.getCmd()){
		case ADD_USER:
			return new AddUserCommand(cmd.getArguments());
		case POST:
			return new PostCommand(cmd.getArguments());
		case FOLLOW:
			return new FollowCommand(cmd.getArguments());
		case STOP_FOLLOWING:
			return new StopFollowingCommand(cmd.getArguments());
		case ADD_AUTH:
			return new AddAuthCommand(cmd.getArguments());
		case DELETE_AUTH:
			return new DeleteAuthCommand(cmd.getArguments());
		default:
			throw new Exception(); //should not happen
		
		}
	}
}
