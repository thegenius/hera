package com.lvonce.hera;
import com.lvonce.hera.logger.RpcLogger;

import java.io.File;
import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.WeakHashMap;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyClassLoader;

import java.lang.SecurityException;
import java.lang.NoSuchMethodException;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.InvocationHandler;  
import java.lang.reflect.Method;  
import java.lang.reflect.Proxy;

public class GroovyFactory implements FileWatcherHandler {

	private static class EntityProxy<T> implements InvocationHandler {
		private T target;
		public EntityProxy(T target) {
			this.target = target;
		}	
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			System.out.println("---- before ---");
			System.out.println("---- call with target +"+target +" ---");
			Object result = method.invoke(this.target, args);
			System.out.println("---- after ---");
			return result;
		}
		public void setTarget(T target) {
			RpcLogger.debug(getClass(), "setTarget: " + target);
			this.target = target;
		}
		public T getTarget() {
			return this.target;
		}
		public T getProxy() {
			return (T)Proxy.newProxyInstance(
				Thread.currentThread().getContextClassLoader(),
				this.target.getClass().getInterfaces(),
				this);
		}
	}

	private static class ClassManager<T> {

		private final String classFilePath;
		private final Class<T> interfaceType;
		private final Map<EntityProxy<T>, Object[]> objectRefs;
		private Class<T> implementClass;
		
		public ClassManager(Class<T> interfaceType, String filePath) {
			this.classFilePath = filePath;
			this.interfaceType = interfaceType;
			this.objectRefs = new WeakHashMap<EntityProxy<T>, Object[]>();
			try {
				RpcLogger.info(getClass(), "try load: " + classFilePath);
				this.implementClass = (Class<T>)Class.forName(classFilePath);
				RpcLogger.info(getClass(), "find pre-defined class");
			} catch (Exception e) {
				e.printStackTrace();
				try {
					filePath = filePath.replace(".", "/");
					GroovyCodeSource source = new GroovyCodeSource(new File(filePath));
					this.implementClass = (Class<T>)GroovyFactory.groovyClassLoader.parseClass(source, false);
				} catch (Exception ex) {
					ex.printStackTrace();
					this.implementClass = null;
				}
			}
		}
		
		public void load() {
			load(new File(this.classFilePath));	
		}
		
		public void load(File file) {
			try {
				RpcLogger.debug(getClass(), "load " + file.toString());
				GroovyCodeSource source = new GroovyCodeSource(file);
				this.implementClass = (Class<T>)GroovyFactory.groovyClassLoader.parseClass(source, false);
				Iterator<Map.Entry<EntityProxy<T>, Object[]>> it = this.objectRefs.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<EntityProxy<T>, Object[]> entry = it.next();
					EntityProxy<T> proxy = entry.getKey();
					if (proxy.getTarget() != null) {
						proxy.setTarget(createInstance(entry.getValue())); 
					} 				
				}
			} catch (Exception e) {
				e.printStackTrace();
				this.implementClass = null;
			}
		}

		private T createInstance(Object ...args) {
    		try {
				T obj = null;
				if (this.implementClass != null) {
					if (args == null || args.length == 0) {	
						obj = (T)this.implementClass.newInstance();
					} else {
						Class[] paramTypes = new Class[args.length];	
						for (int i=0; i<args.length; ++i) {
							paramTypes[i] = args[i].getClass();
						}
						Constructor<T> constructor = (Constructor<T>)this.implementClass.getDeclaredConstructor(paramTypes);
						obj = constructor.newInstance(args);
					}
				}
				return obj;
    		} catch (InstantiationException | 
						IllegalAccessException | 
						SecurityException |
						InvocationTargetException | 
						NoSuchMethodException e) {
        		e.printStackTrace();
        		return null;
 			}
		}

		public T newInstance(Object ...args) {
			T obj = createInstance(args);
			if (obj == null) {
				return null;
			}
			EntityProxy<T> proxy = new EntityProxy(obj);
			this.objectRefs.put(proxy, args);
			return proxy.getProxy();
		}
	}


	private static final GroovyClassLoader groovyClassLoader 
		= new GroovyClassLoader(GroovyFactory.class.getClassLoader());	


	private static final Map<String, ClassManager<?>> classManagerMap
		= new LinkedHashMap<String, ClassManager<?>>();

	//public static void register(Class<?> interfaceType, String classPath) {
	//	classManagerMap.put(classPath, new ClassManager(interfaceType, classPath));
	//}
	
	private static GroovyFactory instance = null;

	private GroovyFactory() {}
	
	public static GroovyFactory getInstance() {
		if (instance == null) {
			instance = new GroovyFactory();
		}
		return instance;
	}
	
	public void updateByFile(File file) {
		try {
			String filePath = file.getCanonicalPath();
			if (filePath.endsWith(".groovy")) {
				RpcLogger.info(getClass(), "reload: " + filePath); 
				ClassManager<?> manager = classManagerMap.get(filePath);
				if (manager != null) {
					manager.load(file);	
				} else {
					RpcLogger.debug(getClass(), "manager is null");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void reload(String classFilePath) {
		try {
			classFilePath = new File(classFilePath).getCanonicalPath();
			ClassManager<?> manager = classManagerMap.get(classFilePath);
			if (manager != null) {
				manager.load();	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public static<T> T newInstance(Class<T> interfaceType, String filePath, Object... args) {
		try {
			if (filePath.contains("/") || filePath.contains("\\")) {
				filePath = new File(filePath).getCanonicalPath();
			}
			ClassManager<T> manager = (ClassManager<T>)classManagerMap.get(filePath);
			if (manager == null) {
				manager = new ClassManager(interfaceType, filePath);
				classManagerMap.put(filePath, manager);
			}
			return manager.newInstance(args);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}

