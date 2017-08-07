package com.lvonce.hera.exception;

public class RpcMethodNotFoundException extends RpcException {
	public RpcMethodNotFoundException(String requestString) {
		super("request: " + requestString + " method is not found in current service interface!");
	}
}
