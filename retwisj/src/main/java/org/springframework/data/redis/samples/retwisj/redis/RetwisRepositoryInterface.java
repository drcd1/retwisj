/*
 * Copyright 2011 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.redis.samples.retwisj.redis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;


import org.springframework.data.redis.samples.retwisj.command.*;


import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.data.redis.samples.retwisj.Range;
import org.springframework.data.redis.samples.retwisj.replication.BroadcasterThrift;
import org.springframework.data.redis.samples.retwisj.replication.Broadcaster;
import org.springframework.data.redis.samples.retwisj.replication.BroadcasterGRPC;
import org.springframework.data.redis.samples.retwisj.replication.BroadcasterRest;
import org.springframework.data.redis.samples.retwisj.web.WebPost;



/**
 * Twitter-clone on top of Redis.
 * 
 * @author Costin Leau
 */
@Named
public class RetwisRepositoryInterface {

	private RetwisRepository retwis;
	
	private Broadcaster broadcaster;

	@Inject
	public RetwisRepositoryInterface(RetwisRepository retwis) {
		this.retwis = retwis;	
		broadcaster = new BroadcasterThrift();
	}

	public String addUser(String name, String password) {
		retwis.addUser(name, password);
		broadcaster.broadcast(new CommandData(CommandData.Type.ADD_USER, 
				new ArrayList<String>(Arrays.asList(name, password))));
		
		return addAuth(name);
	}
		
	public void setBlocked(Set<String> blocked){
		retwis.setBlocked(blocked);
		
	}
	
	public List<WebPost> getPost(String pid) {
		return retwis.getPost(pid);
	}

	public List<WebPost> getPosts(String uid, Range range) {
		return retwis.getPosts(uid, range);
	}

	public List<WebPost> getTimeline(String uid, Range range) {
		return retwis.getTimeline(uid, range);
	}

	public Collection<String> getFollowers(String uid) {
		return retwis.getFollowers(uid);
	}

	public Collection<String> getFollowing(String uid) {
		return retwis.getFollowing(uid);
	}

	public List<WebPost> getMentions(String uid, Range range) {
		return retwis.getMentions(uid, range);
	}

	public Collection<WebPost> timeline(Range range) {
		return retwis.timeline(range);
	}
		

	public Collection<String> newUsers(Range range) {
		return retwis.newUsers(range);
	}

	public void post(String username, WebPost post) {
		retwis.post(username, post);

		broadcaster.broadcast(new CommandData(CommandData.Type.POST, 
				new ArrayList<String>(Arrays.asList(username, 
													post.getContent() != null ? post.getContent(): "",
													post.getName() != null ? post.getName(): "",
													post.getReplyTo() != null ? post.getReplyTo(): "",
													post.getReplyPid() != null ? post.getReplyPid(): "",
													post.getPid() != null ? post.getPid(): "",
													post.getTime() != null ? post.getTime(): "",
													post.getTimeArg() != null ? post.getTimeArg(): "")						
													)));	
		
		System.out.println("Broadcasting: post by " + username);
		System.out.println("Posting: " + username + " posts the following: ");
		System.out.println("  content: " + post.getContent());
		System.out.println("  name:    " + post.getName()) ;
		System.out.println("  Reply to:"+post.getReplyTo());
		System.out.println("  ReplyPid:"+post.getReplyPid()); 
		System.out.println("  pid:     "+post.getPid());
		System.out.println("  time:    "+post.getTime());
		System.out.println("  timeArg: " +post.getTimeArg());
		
		
	}

	public String findUid(String name) {
		return retwis.findUid(name);
		
	}

	public boolean isUserValid(String name) {
		return retwis.isUserValid(name);
	}

	public boolean isPostValid(String pid) {
		return retwis.isPostValid(pid);
	}


	public String findName(String uid) {
		return retwis.findName(uid);
	}

	public boolean auth(String user, String pass) {
		return retwis.auth(user, pass);
	}

	public String findNameForAuth(String value) {
		return retwis.findNameForAuth(value);
	}

	public String addAuth(String name) {
		return retwis.addAuth(name);	
	}

	public void deleteAuth(String user) {
		retwis.deleteAuth(user);
	}

	public boolean hasMorePosts(String targetUid, Range range) {
		return retwis.hasMorePosts(targetUid, range);
	}

	public boolean hasMoreTimeline(String targetUid, Range range) {
		return retwis.hasMoreTimeline(targetUid, range);
	}


	public boolean hasMoreTimeline(Range range) {
		return retwis.hasMoreTimeline(range);
	}

	public boolean isFollowing(String uid, String targetUid) {
		return retwis.isFollowing(uid,  targetUid);
		
	}

	public void follow(String uid, String targetUser) {
		retwis.follow(uid, targetUser);
		broadcaster.broadcast(new CommandData(CommandData.Type.FOLLOW, 
				new ArrayList<String>(Arrays.asList(uid, targetUser))));
		
		System.out.println("Broadcasting: following: " + targetUser);
				
	}

	public void stopFollowing(String uid, String targetUser) {
		retwis.stopFollowing(uid, targetUser);
		
		broadcaster.broadcast(new CommandData(CommandData.Type.STOP_FOLLOWING, 
				new ArrayList<String>(Arrays.asList(uid, targetUser))));	
		System.out.println("Broadcasting: stop following: " + targetUser);
		
	}

	public List<String> alsoFollowed(String uid, String targetUid) {
		return retwis.alsoFollowed(uid, targetUid);
	}

	public List<String> commonFollowers(String uid, String targetUid) {
		return retwis.commonFollowers(uid, targetUid);
	}

	public void initializeBroadcaster(){
		broadcaster.initialize();
	}

	public RetwisRepository getRetwis() {
		return retwis;
	}
	
}