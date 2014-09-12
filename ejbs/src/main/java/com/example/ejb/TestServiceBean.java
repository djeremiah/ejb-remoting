package com.example.ejb;

import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.api.TestService;

@Stateless
@Remote(TestService.class)
public class TestServiceBean implements TestService {
	
	Logger LOG = LoggerFactory.getLogger(TestServiceBean.class);

	@Override
	public void execute(String input) {
		LOG.info("Executing with input: " + input);
	}

}
