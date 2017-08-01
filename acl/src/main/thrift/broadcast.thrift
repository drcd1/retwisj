namespace java acl.command

service BroadcastService{
	void send(1:BroadcastCommand cmd),
}

struct BroadcastCommand{
	1: CommandType cmd,
	2: list<string> arguments,
}

enum CommandType{
	BLOCK,
	UNBLOCK
}