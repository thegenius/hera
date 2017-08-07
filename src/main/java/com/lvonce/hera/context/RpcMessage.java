package com.lvonce.hera.context;

import java.util.Optional;
import java.io.IOException;
import com.lvonce.hera.logger.RpcLogger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

/*
 *
 *
 *            RpcMessageContext
 *	                 |
 *			     RpcMessage
 *            _______|________
 *			 |                |
 *        RpcRequest     RpcResponse
 *
 *
 */
public class RpcMessage {
	private static final ObjectMapper mapper = new ObjectMapper();

	// 0 for single request, 1 for batch request
	// 2 for single response, 3 for batch response
	private final int messageType; 
	private final Object content;

	// for jackson
	public RpcMessage() {
		this.messageType = 0;
		this.content = null;
	}
	
	public RpcMessage(int messageType, Object content) {
		this.messageType = messageType;
		this.content = content;
	}
	
	public int getMessageType() {
		return this.messageType;
	}
	
	public Object getContent() {
		return this.content;
	}

	@Override
	public String toString() {
		try {
			return mapper.writeValueAsString(this);	
		} catch (JsonProcessingException e) {
			return "{ \"messageType\": " + messageType + ", \"content\": " + content +"}";
		}
	}

	public static Optional<RpcMessage> fromString(String jsonString) {
		try {		
			return Optional.ofNullable(mapper.readValue(jsonString, RpcMessage.class));
		} catch (IOException e) {
			RpcLogger.info(RpcMessage.class, e.getMessage());
			return Optional.empty();
		}
	}
}

