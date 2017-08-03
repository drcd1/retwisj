namespace java org.springframework.data.redis.samples.retwisj.command

service BroadcastService{
	void send(1:BroadcastCommand cmd),
}

struct BroadcastCommand{
	1: i32 cmd,
	2: list<string> arguments,
}
