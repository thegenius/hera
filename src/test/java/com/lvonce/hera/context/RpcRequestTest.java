
package com.lvonce.hera;

import com.lvonce.hera.context.RpcRequest;

import java.util.Optional;
import org.testng.annotations.Test;
import com.fasterxml.jackson.databind.JsonNode;;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.testng.Assert.*;

public class RpcRequestTest {

	@Test
	public void constructorTest() {
		RpcRequest request = new RpcRequest(1, "scope", "method", "", new Object[]{1, 2, 3});		
		assertNotNull(request);
	}	
	
	@Test
	public void toStringTest() throws Exception {
		RpcRequest request = new RpcRequest(1, "scope", "method", "sig", new Object[]{1, 2, 3});		
		String jsonString = request.toString();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node1 = mapper.readTree(jsonString);
		JsonNode node2 = mapper.readTree("{\"id\":1, \"scopeName\":\"scope\", \"methodName\": \"method\", \"sigName\":\"sig\", \"args\":[1,2,3]} ");
		assertTrue(node1.equals(node2)); 	
	}

	@Test
	public void fromStringTest() throws Exception {
		Optional<RpcRequest> request = RpcRequest.fromString("{\"id\":1, \"scopeName\":\"scope\", \"methodName\": \"method\", \"sigName\":\"sig\", \"args\":[1,2,3]} ");
		assertTrue(request.isPresent());	
	}

}
