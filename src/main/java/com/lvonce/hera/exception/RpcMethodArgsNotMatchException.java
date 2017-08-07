package com.lvonce.hera.exception;

public class RpcMethodArgsNotMatchException extends RpcException {
	public RpcMethodArgsNotMatchException(String requestString) {
		super("request: " + requestString + " args is not matchable in current service interface!");
	}
}
