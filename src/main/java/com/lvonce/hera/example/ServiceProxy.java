
package com.lvonce.hera.example;
import com.lvonce.hera.future.RpcFuture;

public interface ServiceProxy {
	public RpcFuture hello(String name);
}
