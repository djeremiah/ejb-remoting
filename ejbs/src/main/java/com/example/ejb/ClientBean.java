package com.example.ejb;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.example.api.ClientService;
import com.example.api.TestService;

@Stateless
@Remote(ClientService.class)
public class ClientBean implements ClientService{

	private static final Map<String, String> ROUTING_TABLE = new HashMap<String, String>();
	{
		ROUTING_TABLE.put("gateway1", "clusterA");
		ROUTING_TABLE.put("gateway2", "clusterA");
		ROUTING_TABLE.put("gateway3", "clusterA");
		ROUTING_TABLE.put("gateway4", "clusterB");
	}

	public void invoke(String gateway) {
		TestService service = lookupContext(ROUTING_TABLE.get(gateway));
		service.execute("Invoke for gateway " + gateway);
	}

	private TestService lookupContext(String cluster) {
		if (cluster == null) {
			throw new IllegalArgumentException("Invalid gateway");
		}

		Properties p = new Properties();
		p.put("remote.connections", "node1");
		p.put("remote.connection.node1.port", "4447"); 
		p.put("remote.connection.node1.host",
				System.getProperties().get(cluster + ".cluster-host")); 
		p.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED",
				"false"); 
		p.put("remote.connection.node1.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS",
				"false");

		p.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
		p.put("org.jboss.ejb.client.scoped.context", true); 
		TestService service = null;
		try {
			Context context = new InitialContext(p);

			Context ejbRootNamingContext = (Context) context.lookup("ejb:");
			service = (TestService) ejbRootNamingContext
					.lookup("ear-1.0/ejbs-1.0/TestServiceBean!com.example.api.TestService");
		} catch (NamingException e) {
			throw new RuntimeException("Error retrieving service bean", e);
		}
		return service;
	}

}
