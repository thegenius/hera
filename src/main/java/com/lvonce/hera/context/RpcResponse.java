package com.lvonce.hera.context;

import java.util.Optional;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

public class RpcResponse {	
	private static final ObjectMapper mapper = new ObjectMapper();
	private static final Logger logger = LoggerFactory.getLogger(RpcResponse.class);

	private int id;
	private Object result;
	private Throwable throwable;
	private boolean isInvokeSuccess;

	// for jackson
	public RpcResponse() {
		this.id = 0;
		this.result = null;
		this.throwable = null;
		this.isInvokeSuccess = false;
	}

	public RpcResponse(int id, Object resultOrThrowable, boolean isInvokeSuccess) {
        this.id = id;
        this.isInvokeSuccess = isInvokeSuccess;

        if (isInvokeSuccess) {
            this.result = resultOrThrowable;
		} else {
            this.throwable = (Throwable)resultOrThrowable;
		}
    }
	
	public int getId() {
		return this.id;
	}
	
	public Object getResult() {
		return this.result;
	}
	
	public Throwable getThrowable() {
		return this.throwable;
	}

	public boolean isInvokeSuccess(){
		return this.isInvokeSuccess;
	}
	
	@Override
	public String toString() {
		try {
			return mapper.writeValueAsString(this);	
		} catch (JsonProcessingException e) {
			return "{ \"id\": " + id + ", \"result\": " + result + ", \"throwable\": " +  throwable + ", \"isInvokeSuccess\":" + isInvokeSuccess +"}";
		}
	}

	public static Optional<RpcResponse> fromString(String jsonString) {
		try {		
			return Optional.ofNullable(mapper.readValue(jsonString, RpcResponse.class));
		} catch (IOException e) {
			logger.info(e.getMessage());
			return Optional.empty();
		}
	}


}
