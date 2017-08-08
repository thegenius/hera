package com.lvonce;

import com.lvonce.hera.logger.RpcLogger;
import com.lvonce.hera.HeraNode;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.BeforeClass;
import static org.testng.Assert.*;

public class HeraNodeTest {

	private static int state = 0;

	public static interface Service {
		public String hello(String name);
		public int add(int a, int b);
		public void changeState();
		public void updateState(int x);

		public int sum(int[] nums);
		public int sum(int len, Integer[] nums);
	}

	public static class Provider implements Service {
		public String hello(String name) {
			return "Hello " + name;
		}
		public int add(int a, int b) {
			return a + b;
		}
		public void changeState() {
			HeraNodeTest.state ++;
		}
		public void updateState(int x) {
			HeraNodeTest.state = x;
		}
	
		public int sum(int[] nums) {
			int result = 0;
			for (int i=0; i<nums.length; ++i) {
				result += nums[i];
			}
			return result;
		}
		public int sum(int len, Integer[] nums) {
			int result = 0;
			for (int i=0; i<len; ++i) {
				result += nums[i].intValue();
			}
			return result;
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

		service.changeState();
		assertEquals(HeraNodeTest.state, 1);

		service.updateState(-1);
		assertEquals(HeraNodeTest.state, -1);

		int result1 = service.sum(new int[]{1, 2, 3});
		assertEquals(result1, 6);
		int result2 = service.sum(2, new Integer[]{new Integer(16), new Integer(23), new Integer(41)});
		assertEquals(result2, 39);

	}
}

