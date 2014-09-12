package com.example;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import com.example.api.ClientService;

public class Client {

	public static void main(String[] args) {
		Client client = null;
		try {
			client = new Client();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (client != null) {
			client.execute("testing");
		}

	}

	final ClientService testService;

	private Client() throws Exception {
		testService = init();
	}

	public void execute(String input) {
		testService.invoke("gateway1");
	}

	private final ClientService init() throws Exception {
		Properties p = new Properties();
		p.put("remote.connections", "node1");
		p.put("remote.connection.node1.port", "4447"); // the default remoting
														// port, replace if
														// necessary
		p.put("remote.connection.node1.host", "localhost"); // the host, replace
															// if necessary
		p.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED",
				"false"); // the server defaults to SSL_ENABLED=false
		p.put("remote.connection.node1.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS",
				"false");

		p.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
		p.put("org.jboss.ejb.client.scoped.context", true); // enable scoping
															// here

		Context context = new InitialContext(p);
		Context ejbRootNamingContext = (Context) context.lookup("ejb:");
		return (ClientService) ejbRootNamingContext
				.lookup("ear-1.0/ejbs-1.0/ClientBean!com.example.api.ClientService");
	}

}
