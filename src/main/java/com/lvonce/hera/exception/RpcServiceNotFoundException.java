package com.lvonce.hera.exception;

public class RpcServiceNotFoundException extends RpcException {
	public RpcServiceNotFoundException(String requestString) {
		super("request: " + requestString + " service is not found in current service interface!");
	}
}
