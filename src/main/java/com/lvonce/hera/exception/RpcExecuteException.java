package com.lvonce.hera.exception;

public class RpcExecuteException extends RpcException {
	public RpcExecuteException(String requestString, String exceptionString) {
		super("request: " + requestString + " run with exception: " + exceptionString);
	}
}
