package com.lvonce.hera.exception;

public class RpcTimeoutException extends RpcException {
	public RpcTimeoutException(String requestString) {
		super("request: " + requestString + " time out when calling a Rpc Invoke!");
	}
}
