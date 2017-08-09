package com.lvonce.hera;

import com.lvonce.hera.logger.RpcLogger;
import com.lvonce.hera.HeraNode;

public class App {

	public static interface Service {
		public String hello(String name);
	}

	public static class Provider implements Service {
		public String hello(String name) {
			return "Hello " + name;
		}
	}

	public static void main(String[] args) {
		if (args[0].equals("a")) {
			HeraNode.exports(new Provider(), Service.class);
			HeraNode.start(3721);
		}

		if (args[0].equals("b")) {
			Service service = HeraNode.imports(Service.class, "127.0.0.1", 3721);
			String result = null;
			for (int i=0; i<100000; ++i) {
				result = service.hello("World!");
			}
			RpcLogger.info(App.class, result);
		}
	}
}
