package com.lvonce.hera;

import com.lvonce.hera.context.RpcRequest;
import com.lvonce.hera.exception.RpcExecuteException;
import com.lvonce.hera.exception.RpcTimeoutException;
import com.lvonce.hera.exception.RpcMethodNotFoundException;
import com.lvonce.hera.exception.RpcServiceNotFoundException;
import com.lvonce.hera.exception.RpcMethodArgsNotMatchException;

import java.util.Optional;
import org.testng.annotations.Test;
import com.fasterxml.jackson.databind.JsonNode;;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.testng.Assert.*;

public class RpcExceptionTest {

	@Test 
	public void toStringTest() {
		System.out.println("exception toString() test");
		RpcRequest request = new RpcRequest(1, "scope", "method", "sig", new Object[]{1, 2, 3});		
		String requestString = request.toString();

		RpcExecuteException e1 = new RpcExecuteException(requestString, "test");
		System.out.println(e1.toString());
		RpcTimeoutException e2 = new RpcTimeoutException(requestString);
		System.out.println(e2.toString());
		RpcMethodNotFoundException e3 = new RpcMethodNotFoundException(requestString);
		System.out.println(e3.toString());
		RpcServiceNotFoundException e4 = new RpcServiceNotFoundException(requestString);
		System.out.println(e4.toString());
		RpcMethodArgsNotMatchException e5 = new RpcMethodArgsNotMatchException(requestString);
		System.out.println(e5.toString());
	}

	@Test
	public void getMessageTest() {
		String str = "hello";
		Object obj = str;
		System.out.println(obj.getClass());
		System.out.println("exception getMessage() test");
		RpcRequest request = new RpcRequest(1, "scope", "method", "sig", new Object[]{1, 2, 3});		
		String requestString = request.toString();

		RpcExecuteException e1 = new RpcExecuteException(requestString, "test");
		System.out.println(e1.getMessage());
		RpcTimeoutException e2 = new RpcTimeoutException(requestString);
		System.out.println(e2.getMessage());
		RpcMethodNotFoundException e3 = new RpcMethodNotFoundException(requestString);
		System.out.println(e3.getMessage());
		RpcServiceNotFoundException e4 = new RpcServiceNotFoundException(requestString);
		System.out.println(e4.getMessage());
		RpcMethodArgsNotMatchException e5 = new RpcMethodArgsNotMatchException(requestString);
		System.out.println(e5.getMessage());
	}
}
