package com.lvonce.hera;

import com.lvonce.hera.logger.RpcLogger;
import com.lvonce.hera.HeraNode;

import io.protostuff.runtime.RuntimeSchema;
import io.protostuff.LinkedBuffer;
import io.protostuff.Schema;
import io.protostuff.ProtobufIOUtil;
import groovy.lang.GroovyClassLoader;
import java.io.File;

public class App {

	public static class Foo {
		private String account;
		private String password;
		private int age;
		public Foo(String a, String p, int age) {
			this.account = a;
			this.password = p;
			this.age = age;
		}
		@Override
		public String toString() {
			return "{\"a\":" + account + "\"pass\":" + password + "\"age\":" + age + "}";
		}
	} 

	public static class FooWrapper {
		private Object foo1;
		private Object foo2;
		public FooWrapper(Foo a, Foo b) {
			this.foo1 = a;
			this.foo2 = b;
		}

		@Override
		public String toString() {
			return foo1.toString() + foo2.toString();
		}
	}

	private static LinkedBuffer buffer = LinkedBuffer.allocate();

	public static interface Service {
		public String hello(String name);
	}

	public static class Provider implements Service {
		public String hello(String name) {
			return "Hello " + name;
		}
	}

	public static void testGroovy() throws Throwable {
		//GroovyFactory.register(IFoo.class, "./Foo.groovy");
		IFoo test = GroovyFactory.newInstance(IFoo.class, "com.lvonce.hera.Test");
		System.out.println(test.hello("wang wei"));
		System.out.println(new File("./Foo.groovy").getCanonicalPath());
		IFoo foo = GroovyFactory.newInstance(IFoo.class, "./Foo.groovy");
		Thread thread = new Thread() {
			public void run() {
				while (true) {
					RpcLogger.info(App.class, foo.hello("wang wei"));
					try { 
						Thread.sleep(2000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		thread.start();
		//ClassLoader classLoader = new App().getClass().getClassLoader();
		//GroovyClassLoader groovyClassLoader = new GroovyClassLoader(classLoader);

		//try {
		//	Class fooClass = groovyClassLoader.parseClass(new File("./Foo.groovy"));
		//	IFoo foo = (IFoo)fooClass.newInstance();
		//	System.out.println(foo.hello("wang wei"));
		//} catch (Exception e) {
		//} 
	}

	public static void testFileWatcher() {
		FileWatcher.register(".", GroovyFactory.getInstance());
		FileWatcher.watch();
	}

	public static void main(String[] args) throws Throwable {
		testGroovy();
		testFileWatcher();



		Foo foo1 = new Foo("Wang", "123", 23);
		Foo foo2 = new Foo("Wang", "123", 24);
		FooWrapper foo = new FooWrapper(foo1, foo2);

		Schema<FooWrapper> schema = RuntimeSchema.getSchema(FooWrapper.class);
		//Schema<Object> schema2 = RuntimeSchema.getSchema(Object.class);
		try {
			byte[] protobuff = ProtobufIOUtil.toByteArray(foo, schema, buffer);
			
			FooWrapper fooDesr = schema.newMessage();
			ProtobufIOUtil.mergeFrom(protobuff, fooDesr, schema);
			RpcLogger.info(App.class, fooDesr.toString());			
		} finally {
			buffer.clear();
		}			



		if (args[0].equals("a")) {
			HeraNode.exports(3721, new Provider(), Service.class);
			HeraNode.serve();
			HeraNode.sync();
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
