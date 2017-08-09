namespace java acl.server

service AclService{
	 void block(1:string id, 2: string targetId, 3: i32 delay),
	 void unblock(1:string id, 2: string targetId),
	 set<string> blocks(1:string id),
	 set<string> blockedBy(1:string id),
	

}