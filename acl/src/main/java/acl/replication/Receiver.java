package acl.replication;

import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

import acl.ACL;
import acl.command.*;

public class Receiver{
	
	private static ACL acl;
	
	public static void run(ACL otherAcl){
		acl = otherAcl;
		
		BroadcastHandler handler = new BroadcastHandler();
		BroadcastService.Processor<BroadcastService.Iface> processor =
					new BroadcastService.Processor<BroadcastService.Iface>(handler);
		
		try{
			TNonblockingServerTransport serverTransport = new TNonblockingServerSocket(5052);
			TServer server = new TNonblockingServer(new TNonblockingServer.Args(serverTransport).processor(processor));
			System.out.println("starting receiver server...");
			server.serve();
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	static class BroadcastHandler implements BroadcastService.Iface {
	    @Override
	    public void send(BroadcastCommand cmd) {
	    	try{
	    		acl.execute(CommandFactory.get(new CommandData(CommandData.getTypeFromInt(
	    															cmd.getCmd()),
	    															cmd.getArguments())
	    									)
	    		);
	    	} catch (Exception e){
	    		e.printStackTrace();
	    	}
	    }
	   
	}
}