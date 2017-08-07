package com.lvonce.hera.exception;

public class RpcException extends RuntimeException {
	private static final long serialVersionUID = -1605324441723957563L;
	public RpcException(String exceptionString) {
		super(exceptionString);
	}
}
