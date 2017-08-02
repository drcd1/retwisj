package acl.command;

import java.util.List;

/*
 * Class for describing commands. Should have a similar structure to the data
 * passed between replicas. Contains utlity methods to convert between the enum CommandType and the
 * integers passed between replicas - passing enums is hard.
 */

public class CommandData {
	public static enum Type{
		BLOCK,
		UNBLOCK
	}
	
	public static int getIntFromType(Type cmd) {
		switch(cmd){
			case BLOCK:
				return 0;
			case UNBLOCK:
				return 1;
			default:
				throw new RuntimeException(); //should not happen
		}
				
	}
	
	public static Type getTypeFromInt(int i) {
		switch(i){
			case 0:
				return Type.BLOCK;
			case 1:
				return Type.UNBLOCK;
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
