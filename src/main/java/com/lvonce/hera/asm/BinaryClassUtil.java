package com.lvonce.hera.asm;

import java.lang.reflect.Constructor;
import java.lang.SecurityException;
import java.lang.NoSuchMethodException;
import java.lang.reflect.InvocationTargetException;

public class BinaryClassUtil {
	@SuppressWarnings("unchecked")
	public static Object newInstance(byte[] bytesOfClass, Object... args) {
    	try {
        	Class<?> classType = new ClassLoader() {
            	public Class<?> defineClass(byte[] bytes) {
                	return super.defineClass(null, bytes, 0, bytes.length);
            	}
        	}.defineClass(bytesOfClass);
			
			if (args == null || args.length == 0) {	
				return classType.newInstance();
			} else {
				Class[] paramTypes = new Class[args.length];	
				for (int i=0; i<args.length; ++i) {
					paramTypes[i] = args[i].getClass();
				}
				Constructor	constructor = classType.getDeclaredConstructor(paramTypes);
				return constructor.newInstance(args);
			}
    	} catch (InstantiationException | 
					IllegalAccessException | 
					SecurityException |
					InvocationTargetException | 
					NoSuchMethodException e) {
        	e.printStackTrace();
        	return null;
 		}
	}
}
