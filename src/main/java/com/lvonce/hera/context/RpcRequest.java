package com.lvonce.hera.context;

import java.util.Optional;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

public class RpcRequest {
	private static final ObjectMapper mapper = new ObjectMapper();
	private static final Logger logger = LoggerFactory.getLogger(RpcRequest.class);

	private final int id;
	private final Object[] args;
	private final String scopeName;
	private final String methodName;
	private final String sigName;

	// for jackson
	public RpcRequest(){
		this.id = 0;
		this.args = null;
		this.scopeName = null;
		this.methodName = null;
		this.sigName = null;
	}
	
	public RpcRequest(int id, String scopeName, String methodName, String sigName, Object[] args) {
		this.id = id;
		this.args = args;
		this.scopeName = scopeName;
		this.methodName = methodName;
		this.sigName = sigName;
	}

	public int getId() {
		return this.id;
	}
	
	public Object[] getArgs() {
		return this.args;
	}

	public String getScopeName() {
		return this.scopeName;
	}

	public String getMethodName() {
		return this.methodName;
	}
	
	public String getSigName() {
		return this.sigName;
	}

	@Override
	public String toString() {
		try {
			return mapper.writeValueAsString(this);	
		} catch (JsonProcessingException e) {
			return "{ \"id\": " + id + ", \"scopeName\": " + scopeName + ", \"methodName\": " +  methodName + "\"sigName\": "+ sigName + ", \"args\": \"unknown\" }";
		}
	}

	public static Optional<RpcRequest> fromString(String jsonString) {
		try {		
			return Optional.ofNullable(mapper.readValue(jsonString, RpcRequest.class));
		} catch (IOException e) {
			logger.info(e.getMessage());
			return Optional.empty();
		}
	}
}
