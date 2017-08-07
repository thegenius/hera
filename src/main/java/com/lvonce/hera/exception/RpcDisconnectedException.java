
package com.lvonce.hera.exception;

public class RpcDisconnectedException extends RpcException {
	public RpcDisconnectedException(String host, int port) {
		super("connection to [" + host + ": " + port + "] is disconnected!");
	}
}
