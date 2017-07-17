package org.springframework.data.redis.samples.retwisj.remote;

import java.util.Set;

public interface ACLInterface {
	
	/**
	 * Blocks an user
	 *
	 * @param uid The uid of the user blocking.
	 * @param targetUid The uid of the blocked user
	 */
	public void block(String uid, String targetUid);
	
	
	
	/**
	 * Blocks an user
	 *
	 * @param uid The uid of the user blocking.
	 * @param targetUid The uid of the blocked user
	 */
	public void unblock(String uid, String targetUid);
	
	/**
	 * Returns the set of uids blocked by an user
	 *
	 * @param uid The uid of the user.
	 * @return Returns the uids blocked by the user
	 */
	public Set<String> blocks(String uid);
	
	public Set<String> blockedBy(String uid);
} 
