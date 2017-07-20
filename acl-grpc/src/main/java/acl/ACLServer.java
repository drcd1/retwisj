package acl;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ACLServer implements CommandLineRunner {
	
	private static final Logger logger = Logger.getLogger(ACLServer.class.getName());

	private Server server;
	
	@Autowired
	private ACL acl;
	
	public ACLServer() {
	}
	
	
	public void run(String... args) throws Exception {
		start();
	    blockUntilShutdown();
		
	}
	private void start() throws IOException {
	    /* The port on which the server should run */
	    int port = 8084;
	    server = ServerBuilder.forPort(port).addService(new ACLServiceImpl(acl))
	        .build()
	        .start();
	    System.out.println(port + ": port");
	    logger.info("Server started, listening on " + port);
	    Runtime.getRuntime().addShutdownHook(new Thread() {
	      @Override
	      public void run() {
	        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
	        System.err.println("*** shutting down gRPC server since JVM is shutting down");
	        ACLServer.this.stop();
	        System.err.println("*** server shut down");
	      }
	    });
	}
	
	private void stop() {
		if (server != null) {
			server.shutdown();
		}
	}
	
	/**
	 * Await termination on the main thread since the grpc library uses daemon threads.
	 */
	private void blockUntilShutdown() throws InterruptedException {
		if (server != null) {
			server.awaitTermination();
		}
	}

	
	static class ACLServiceImpl extends ACLServiceGrpc.ACLServiceImplBase {
		
		private ACL acl;
		
		public ACLServiceImpl(ACL acl){
			this.acl = acl;
		}
		
	    @Override
	    public void block(Uids uids, StreamObserver<Empty> responseObserver) {
	      Empty reply = Empty.newBuilder().build();
	      
	      acl.block(uids.getId(), uids.getTargetId());
	      
	      responseObserver.onNext(reply);
	      responseObserver.onCompleted();
	    }
	    
	    @Override
	    public void unblock(Uids uids, StreamObserver<Empty> responseObserver){
		      Empty reply = Empty.newBuilder().build();
		      
		      acl.unblock(uids.getId(), uids.getTargetId());
		      
		      responseObserver.onNext(reply);
		      responseObserver.onCompleted();
		}
	    
	    @Override
	    public void blocks(Uid uid, StreamObserver<UidSet> responseObserver){
	    	
	    	  
		      UidSet reply = UidSet.newBuilder().addAllIds(acl.blocks(uid.getId())).build();
		      
		      responseObserver.onNext(reply);
		      responseObserver.onCompleted();
		}
	    
	    @Override
	    public void blockedBy(Uid uid, StreamObserver<UidSet> responseObserver){
	    	UidSet reply = UidSet.newBuilder().addAllIds(acl.blockedBy(uid.getId())).build();
		      
		      responseObserver.onNext(reply);
		      responseObserver.onCompleted();
		}

	}
}
