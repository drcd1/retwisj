import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class Registry{
	private static HashSet<String> aclAddrs = new HashSet<String>();
	
	public static void main(String[] args){
		try {
			BufferedReader r = new BufferedReader(new FileReader("acl.txt"));
			String currentLine;
			while ((currentLine = r.readLine()) != null) {
				if(!currentLine.isEmpty()){
					aclAddrs.add(currentLine);
				}
			}
			
			if(r!=null){
				r.close();
			}
		} catch (IOException e){
			e.printStackTrace();
		}
		
		for(String hostAddr: aclAddrs){
			System.out.println("Registering " + hostAddr + "..." );
		
			TTransport transport = new TSocket(hostAddr, 5051);
			
			TProtocol protocol = new TBinaryProtocol(transport);
			RegistryService.Client cl = new RegistryService.Client(protocol);
			
			
			while(!openTransport(transport)){
				System.out.println("Sleeping...");
				try{
					TimeUnit.SECONDS.sleep(5);
				} catch (InterruptedException e){
					e.printStackTrace();
				}
			}
			
			for(String addr: aclAddrs){
				if(addr != hostAddr){
					try{
						cl.log(addr);
					} catch (Exception e){
						e.printStackTrace();
					}
				}
			}
			
			transport.close();
			System.out.println("Registered " + hostAddr);
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
}