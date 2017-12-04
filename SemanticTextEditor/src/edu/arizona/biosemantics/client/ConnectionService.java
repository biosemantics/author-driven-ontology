package edu.arizona.biosemantics.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;



/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("connect")
public interface ConnectionService extends RemoteService {
	String greetServer(String name) throws IllegalArgumentException;
}