package com.lvonce;

import com.lvonce.hera.logger.RpcLogger;
import com.lvonce.hera.HeraNode;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.BeforeClass;
import static org.testng.Assert.*;

public class HeraNodeTest {

	public static interface Service {
		public String hello(String name);
		public int add(int a, int b);
	}

	public static class Provider implements Service {
		public String hello(String name) {
			return "Hello " + name;
		}
		public int add(int a, int b) {
			return a + b;
		}
	}

	

	@BeforeClass
	public static void startServer() {
		Thread thread = new Thread() {
			public void run() {
				HeraNode.exports(new Provider(), Service.class);
				HeraNode.start(3721);
			}
		};
		thread.start();
	}

	@Test
	public void test() {
		Service service = HeraNode.imports(Service.class, "127.0.0.1", 3721);
		String result = service.hello("World!");
		assertEquals(result, "Hello World!");	
		int sum = service.add(1, 2);
		assertEquals(sum, 3);
	}
}

