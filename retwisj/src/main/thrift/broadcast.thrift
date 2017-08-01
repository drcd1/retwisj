namespace java org.springframework.data.redis.samples.retwisj.command

service BroadcastService{
	void send(1:BroadcastCommand cmd),
}

struct BroadcastCommand{
	1: CommandType cmd,
	2: list<string> arguments,
}

enum CommandType{
	ADD_USER,
	POST,
	FOLLOW,
	STOP_FOLLOWING,
	ADD_AUTH,
	DELETE_AUTH
}