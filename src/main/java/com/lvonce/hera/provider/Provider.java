package com.lvonce.hera.provider;

public interface Provider {
	public Object call(String methodName, String signature, Object[] args);
}
