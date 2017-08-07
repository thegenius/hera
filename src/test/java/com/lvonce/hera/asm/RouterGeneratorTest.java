package com.lvonce.hera.asm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvonce.hera.future.RpcFuture;
import java.lang.reflect.Method;
import org.testng.annotations.Test;
import com.fasterxml.jackson.databind.ObjectMapper;  
import static org.testng.Assert.*;

public class RouterGeneratorTest {
    private final Logger logger = LoggerFactory.getLogger(RouterGenerator.class);

	public static interface Service {
		public void func1();
		public void func2(int a);
		public void func3(int[] a);
		public void func4(Integer[] a);
	}

	public static interface ServiceProxy {
		public RpcFuture func1();
		public RpcFuture func2(int a);
		public RpcFuture func3(int[] a);
		public RpcFuture func4(Integer[] a);
	}

	public class Stub {
		//public Object call(String scope, String name, String sig, Object... args) {
		public Object call(String name, String sig, Object... args) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				String argsString = mapper.writeValueAsString(args);
				//logger.info("proxy call(" +scope+", "+name+", "+sig+", " + argsString+")");
				logger.info("proxy call(" +name+", "+sig+", " + argsString+")");
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}	
		}
	}

	public interface Forker {
		public Object call(String methodName, String sigName, Object... args);
	}	

	public class Handler {
		public void func1() {
			logger.info("handler func1");
		} 
		public void func2(int a) throws Exception {
			ObjectMapper mapper = new ObjectMapper();
			String argsString = mapper.writeValueAsString(a);
			logger.info("handler func2("+argsString+")");
		} 
		public void func3(int[] a) throws Exception {
			ObjectMapper mapper = new ObjectMapper();
			String argsString = mapper.writeValueAsString(a);
			logger.info("handler func3("+argsString+")");
		} 
		public void func4(Integer a) throws Exception {
			ObjectMapper mapper = new ObjectMapper();
			String argsString = mapper.writeValueAsString(a);
			logger.info("handler func4("+argsString+")");
		} 
		public void func5(Integer[] a) throws Exception {
			ObjectMapper mapper = new ObjectMapper();
			String argsString = mapper.writeValueAsString(a);
			logger.info("handler func5("+argsString+")");
		} 
		public void func6(Integer[] a, Integer[] b) throws Exception {
			ObjectMapper mapper = new ObjectMapper();
			String argsStringA = mapper.writeValueAsString(a);
			String argsStringB = mapper.writeValueAsString(b);
			logger.info("handler func6("+argsStringA+","+argsStringB+")");
		} 
	}


	@Test
	public void joinRouterTest() {
		try {
			Service proxy = RouterGenerator.getJoinRouter(Service.class, new Stub());
			proxy.func1();
			proxy.func2(23);
			proxy.func3(new int[]{23, 41});
			proxy.func4(new Integer[]{new Integer(23)});
			
			RpcFuture future;
			ServiceProxy serviceProxy = RouterGenerator.getJoinRouter(ServiceProxy.class, new Stub());
			future = serviceProxy.func1();
			future = serviceProxy.func2(23);
			future = serviceProxy.func3(new int[]{23, 41});
			future = serviceProxy.func4(new Integer[]{new Integer(23)});
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void forkRouterTest() {
		try {
			Forker dispatcher = RouterGenerator.getForkRouter(Forker.class, new Handler());
			logger.info("fork test begin");
			//dispatcher.call("func1", "()V");
			//dispatcher.call("func2", "(I)V", 23);
			//dispatcher.call("func3", "([I)V", new int[]{23, 41});
			//dispatcher.call("func4", "(Ljava/lang/Integer;)V", new Integer(23));
			//dispatcher.call("func5", "([Ljava/lang/Integer;)V", new Integer[]{new Integer(23)});
			//dispatcher.call("func6", "([Ljava/lang/Integer;[Ljava/lang/Integer;)V", new Integer[]{new Integer(23)},new Integer[]{new Integer(23)});
			dispatcher.call("func1", "()");
			dispatcher.call("func2", "(I)", 23);
			dispatcher.call("func3", "([I)", new int[]{23, 41});
			dispatcher.call("func4", "(Ljava/lang/Integer;)", new Integer(23));
			dispatcher.call("func5", "([Ljava/lang/Integer;)", new Integer[]{new Integer(23)});
			dispatcher.call("func6", "([Ljava/lang/Integer;[Ljava/lang/Integer;)", new Integer[]{new Integer(23)},new Integer[]{new Integer(23)});
			logger.info("fork test end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

