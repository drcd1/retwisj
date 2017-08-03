package org.springframework.data.redis.samples.retwisj.command;

import java.util.List;

/*
 * Class for describing commands. Should have a similar structure to the data
 * passed between replicas. Contains utlity methods to convert between the enum CommandType and the
 * integers passed between replicas - passing enums is hard.
 */

public class CommandData {
	public static enum Type{
		ADD_USER,
		POST,
		FOLLOW,
		STOP_FOLLOWING,
		ADD_AUTH,
		DELETE_AUTH
	}
	
	public static int getIntFromType(Type cmd) {
		switch(cmd){
		case ADD_USER:
			return 0;
		case POST:
			return 1;
		case FOLLOW:
			return 2;
		case STOP_FOLLOWING:
			return 3;
		case ADD_AUTH:
			return 4;
		case DELETE_AUTH:
			return 5;
		default:
			throw new RuntimeException(); //should not happen
		}
				
	}
	
	public static Type getTypeFromInt(int i) {
		switch(i){
			case 0:
				return Type.ADD_USER;
			case 1:
				return Type.POST;
			case 2:
				return Type.FOLLOW;
			case 3:
				return Type.STOP_FOLLOWING;
			case 4:
				return Type.ADD_AUTH;
			case 5:
				return Type.DELETE_AUTH;
			default:
				throw new RuntimeException(); //should not happen
		}
	}
	
	public CommandData(Type cmd, List<String> arguments){
		this.cmd = cmd;
		this.arguments = arguments;
	}
	
	public Type getCmd(){
		return cmd;
	}
	
	public List<String> getArguments(){
		return arguments;
	}
	
	private Type cmd;
	private List<String> arguments;
	
	

}
