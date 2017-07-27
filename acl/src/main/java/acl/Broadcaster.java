package acl;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

public class Broadcaster{
	private static HashSet<BroadcastService.Client> replicas = new HashSet<BroadcastService.Client>();
	
	public static void run(){
		RegistryHandler handler = new RegistryHandler();
		RegistryService.Processor<RegistryService.Iface> processor =
					new RegistryService.Processor<RegistryService.Iface>(handler);
		try{
			TServerTransport serverTransport = new TServerSocket(5051);		
			TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));
			System.out.println("starting server to receive other replicas...");
			server.serve();
		} catch (TTransportException e){
			e.printStackTrace();
		}
		
	}
	
	static class RegistryHandler implements RegistryService.Iface {
				
	    @Override
	    public void log(String hostAddr) {
	    	try {
	    		System.out.println("Will add " + hostAddr);
				TTransport transport = new TFramedTransport(new TSocket(hostAddr, 5052));

				while(!openTransport(transport)){
					System.out.println("Sleeping...");
					
					TimeUnit.SECONDS.sleep(5);
					
				}

				
				TProtocol protocol = new TBinaryProtocol(transport);
				BroadcastService.Client cl = new BroadcastService.Client(protocol);
				replicas.add(cl);
				
				System.out.println("Added " + hostAddr);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	   
	}
	
	private static boolean openTransport(TTransport transport){
		try{
			transport.open();
			return true;
		} catch(Exception e){
			return false;
		}
	}
	
	
	//should return sucess/failure?
	public static void broadcast(String id){
		for(BroadcastService.Client cl: replicas){
			try{
				cl.send(id + " says hi!" );
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}