package com.lvonce.hera.asm;

import java.util.Map;
import java.util.UUID;
import java.util.LinkedHashMap;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Label;

import static org.objectweb.asm.Opcodes.*;
import static com.lvonce.hera.asm.ASMInsnMapper.*;
import static com.lvonce.hera.asm.BinaryClassUtil.*;

public class RouterGenerator {
	
	private static Class<?> defineProxyInterface(Class<?> interfaceType, Class<?> returnType) {
		ClassWriter cw = new ClassWriter(0);
		FieldVisitor fv;
		MethodVisitor mv;

		String interfaceName = interfaceType.getName().replace('.', '/');

		cw.visit(52, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, interfaceName + "Proxy", null, "java/lang/Object", null);
		//mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "hello", "(Ljava/lang/String;)V", null, null);
		//mv.visitEnd();
		Method[] methods = interfaceType.getDeclaredMethods();
		for (Method method: methods) {
			String methodName = method.getName().replace('.', '/');	
			String sigName = SignatureUtil.getSignature(method, returnType);
			mv = cw.visitMethod(Opcodes.ACC_PUBLIC, methodName, sigName, null, null);
			mv.visitEnd();
		}
		cw.visitEnd();

		byte[] bytesOfClass = cw.toByteArray();
		Class<?> proxyInterfaceType = new ClassLoader() {
            	public Class<?> defineClass(byte[] bytes) {
                	return super.defineClass(null, bytes, 0, bytes.length);
            	}
        	}.defineClass(bytesOfClass);
		return proxyInterfaceType;
	}
	
	public static<T> T getJoinRouter(Class<T> interfaceType, Object provider) throws NoSuchMethodException {
		return (T)getJoinRouter(interfaceType, provider, "call");
	}

	public static<T> T getJoinRouter(Class<T> interfaceType, Object provider, String funcName) throws NoSuchMethodException {
		String interfaceName = interfaceType.getName().replace('.', '/');
		String routerName = interfaceName + UUID.randomUUID().toString().replace('-', '_');
		Class<?> providerClass = provider.getClass();
		String providerClassName = providerClass.getName().replace('.', '/');

		//Class[] joinMethodParamTypes = new Class[]{String.class, String.class, String.class, Object[].class}; 
		Class[] joinMethodParamTypes = new Class[]{String.class, String.class, Object[].class}; 
		Method joinMethod = providerClass.getDeclaredMethod(funcName, joinMethodParamTypes);
		 	

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		FieldVisitor fv = null;
		MethodVisitor mv = null;
		cw.visit(Opcodes.V1_8,
				Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
				routerName,
				null,
				"java/lang/Object", 
				new String[]{interfaceName});

		{
			fv = cw.visitField(ACC_PRIVATE + ACC_FINAL, "service", "L"+providerClassName+";", null, null);
			fv.visitEnd();
		}


		//{
		//	mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		//	mv.visitCode();
		//	mv.visitVarInsn(ALOAD, 0);
		//	mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		//	mv.visitInsn(RETURN);
		//	mv.visitMaxs(1, 1);
		//	mv.visitEnd();
		//}

		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(L"+providerClassName+";)V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitFieldInsn(PUTFIELD, routerName, "service", "L"+providerClassName+";");
			mv.visitInsn(RETURN);
			mv.visitMaxs(2, 2);
			mv.visitEnd();
		}

		Method[] methods = interfaceType.getDeclaredMethods();
		for (Method method: methods) {
			String methodName = method.getName().replace('.', '/');	
			String sigName = SignatureUtil.getSignature(method);
			String sigNameWithoutReturnType = SignatureUtil.getSignatureWithoutReturnType(method);
    		Class<?>[] parameterTypes = method.getParameterTypes();
			int paramLength = parameterTypes.length;

			mv = cw.visitMethod(Opcodes.ACC_PUBLIC, methodName, sigName, null, null);
			mv.visitCode();
			
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, routerName, "service", "L"+providerClassName+";");
			
			//mv.visitLdcInsn(interfaceName);
			mv.visitLdcInsn(methodName);
			mv.visitLdcInsn(sigNameWithoutReturnType);
		
			pushConstInsn(mv, paramLength);
			mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
			
			for (int i=0; i<paramLength; ++i) {
				String paramName = parameterTypes[i].getName().replace('.', '/');
				mv.visitInsn(DUP);
				pushConstInsn(mv, i);
				loadInsn(mv, paramName, i+1);
				boxInsn(mv, paramName);
				mv.visitInsn(AASTORE);
			}

			//mv.visitMethodInsn(INVOKEVIRTUAL, providerClassName, funcName, "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;", false);
			mv.visitMethodInsn(INVOKEVIRTUAL, providerClassName, funcName, "(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;", false);
			String returnTypeName = method.getReturnType().getName().replace('.', '/');
			castInsn(mv, returnTypeName);
			unboxInsn(mv, returnTypeName);
			returnInsn(mv, returnTypeName);
			mv.visitMaxs(7, 3);
			mv.visitEnd();
		}

        cw.visitEnd();

 		byte[] data = cw.toByteArray();
		return (T)BinaryClassUtil.newInstance(data, provider);
	}

	private static void writeToFile(String fileName, byte[] data) {
		try {
        	File file = new File(fileName);
        	FileOutputStream fout = new FileOutputStream(file);
        	fout.write(data);
        	fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//public static<T, E> T getForkRouter(Class<T> dispatcherInterface, Class<E> interfaceType, E provider) throws Exception {
	//	String providerClassName = provider.getClass().getName().replace('.', '/');
	//	String interfaceName = interfaceType.getName().replace('.', '/');
	//	String routerName = providerClassName + UUID.randomUUID().toString().replace('-', '_');
	//	String dispatcherName = dispatcherInterface.getName().replace('.', '/');
	//	ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
	//	FieldVisitor fv;
	//	MethodVisitor mv;

	//	cw.visit(52, ACC_PUBLIC + ACC_SUPER, routerName, null, "java/lang/Object", new String[]{dispatcherName});
	//	{
	//		fv = cw.visitField(ACC_PRIVATE + ACC_FINAL, "service", "L"+providerClassName+";", null, null);
	//		fv.visitEnd();
	//	}

	//	{
	//		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(L"+providerClassName+";)V", null, null);
	//		mv.visitCode();
	//		mv.visitVarInsn(ALOAD, 0);
	//		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
	//		mv.visitVarInsn(ALOAD, 0);
	//		mv.visitVarInsn(ALOAD, 1);
	//		mv.visitFieldInsn(PUTFIELD, routerName, "service", "L"+providerClassName+";");
	//		mv.visitInsn(RETURN);
	//		mv.visitMaxs(2, 2);
	//		mv.visitEnd();
	//	}

	//	{
	//		mv = cw.visitMethod(ACC_PUBLIC, "call", "(L"+interfaceName+";Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;", null, null);
	//		mv.visitCode();
	//		Method[] methods = provider.getClass().getDeclaredMethods();

	//		Label nextLabel = new Label();
	//		for (int i=0; i<methods.length; ++i) {
	//			Method method = methods[i];
	//			String methodName = method.getName().replace('.', '/');	
	//			String sigName = SignatureUtil.getSignature(method);
    //			Class<?>[] parameterTypes = method.getParameterTypes();
	//			int paramLength = parameterTypes.length;
	//	
	//			if (i != 0) {
	//				mv.visitLabel(nextLabel);
	//				mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
	//				nextLabel = new Label();
	//			}
	//			
	//			{
	//				mv.visitVarInsn(ALOAD, 2);
	//				mv.visitLdcInsn(methodName + "." + sigName);
	//				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
	//				mv.visitJumpInsn(IFEQ, nextLabel);
	//				mv.visitVarInsn(ALOAD, 1);
	//				
	//				for (int j=0; j<paramLength; ++j) {
	//					String paramName = parameterTypes[j].getName().replace('.', '/');
	//					mv.visitVarInsn(ALOAD, 3);
	//					pushConstInsn(mv, j);
	//					mv.visitInsn(AALOAD);
	//					castInsn(mv, paramName);
	//					unboxInsn(mv, paramName);
	//				}

	//				mv.visitMethodInsn(INVOKEINTERFACE, interfaceName, methodName, sigName, true);

	//				String returnTypeName = method.getReturnType().getName().replace('.', '/');
	//				if (returnTypeName.equals("void")) {
	//					mv.visitInsn(ACONST_NULL);
	//					mv.visitInsn(ARETURN);
	//				} else {
	//					boxInsn(mv, returnTypeName);
	//					mv.visitInsn(ARETURN);
	//				}
	//			}	

	//			if (i == methods.length-1) {
	//				mv.visitLabel(nextLabel);
	//				mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
	//				mv.visitInsn(ACONST_NULL);
	//				mv.visitInsn(ARETURN);
	//				mv.visitMaxs(4, 3);
	//				mv.visitEnd();
	//			}	
	//		}
	//	}
	//	cw.visitEnd();
	//	writeToFile("dispather.class", cw.toByteArray());
	//	return (T)BinaryClassUtil.newInstance(cw.toByteArray(), provider);
	//}

	

	public static<T, E> T getForkRouter(Class<T> dispatcherInterface, E provider) {
		String providerClassName = provider.getClass().getName().replace('.', '/');
		String routerName = providerClassName + UUID.randomUUID().toString().replace('-', '_');
		String dispatcherName = dispatcherInterface.getName().replace('.', '/');

		Method[] methods = provider.getClass().getDeclaredMethods();
		LinkedHashMap<String, LinkedHashMap<String, Method>> methodMap = new LinkedHashMap<String, LinkedHashMap<String, Method>>();
		for (Method method: methods) {
			int modifier = method.getModifiers();
			if (Modifier.isPublic(modifier)) {
				String methodName = method.getName().replace('.', '/');
				String sigName = SignatureUtil.getSignatureWithoutReturnType(method);
				LinkedHashMap<String, Method> sigMethodMap = methodMap.get(methodName);
				if (sigMethodMap == null) {
					sigMethodMap = new LinkedHashMap<String, Method>();
					methodMap.put(methodName, sigMethodMap);
				}
				sigMethodMap.put(sigName, method);
			}
		}

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		FieldVisitor fv;
		MethodVisitor mv;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, routerName, null, "java/lang/Object", new String[]{dispatcherName});
		{
			fv = cw.visitField(ACC_PRIVATE + ACC_FINAL, "service", "L"+providerClassName+";", null, null);
			fv.visitEnd();
		}

		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(L"+providerClassName+";)V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitFieldInsn(PUTFIELD, routerName, "service", "L"+providerClassName+";");
			mv.visitInsn(RETURN);
			mv.visitMaxs(2, 2);
			mv.visitEnd();
		}

		{
			mv = cw.visitMethod(ACC_PUBLIC, "call", "(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;", null, null);
			mv.visitCode();

			Label nextMethodLabel = new Label();
			Label nextSigLabel = new Label();
			Label lastLabel = new Label();
			int methodCount = methodMap.size();
			int methodIndex = 0;
			int sigIndex = 0;
			for (Map.Entry<String, LinkedHashMap<String, Method>> entry: methodMap.entrySet()) {

				if (methodIndex != 0) {
					mv.visitLabel(nextMethodLabel);
					mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
					nextMethodLabel = new Label();
				}
	
				String methodName = entry.getKey();
				LinkedHashMap<String, Method> sigMethodMap = entry.getValue();
				int sigCount = sigMethodMap.size();
				for (Map.Entry<String, Method> sigMethodEntry : sigMethodMap.entrySet()) {
					String sigNameWithoutReturnType = sigMethodEntry.getKey();
					Method method = sigMethodEntry.getValue();	
					String sigName = SignatureUtil.getSignature(method);
					Class<?>[] parameterTypes = method.getParameterTypes();
					int paramLength = parameterTypes.length;
		
					if (sigIndex == 0) {
						mv.visitVarInsn(ALOAD, 1);
						mv.visitLdcInsn(methodName);
						mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
						if (methodIndex == methodCount-1) {
							mv.visitJumpInsn(IFEQ, lastLabel);
						} else {
							mv.visitJumpInsn(IFEQ, nextMethodLabel);
						}
					} else {
						mv.visitLabel(nextSigLabel);
						mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
						nextSigLabel = new Label();
					}

					mv.visitVarInsn(ALOAD, 2);
					mv.visitLdcInsn(sigNameWithoutReturnType);
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
					if (sigIndex == sigCount-1) {
						mv.visitJumpInsn(IFEQ, lastLabel);
					} else {
						mv.visitJumpInsn(IFEQ, nextSigLabel);
					}

					mv.visitVarInsn(ALOAD, 0);
					mv.visitFieldInsn(GETFIELD, routerName, "service", "L"+providerClassName+";");
					boolean singleArrayOfObject = false;
					if (paramLength == 1) {
						String paramTypeName = parameterTypes[0].getName().replace('.', '/');
						if (paramTypeName.startsWith("[L")) {
							singleArrayOfObject = true;
							mv.visitVarInsn(ALOAD, 3);
							mv.visitTypeInsn(CHECKCAST, paramTypeName);
						}
					} 
					if (!singleArrayOfObject) {
						for (int j=0; j<paramLength; ++j) {
							String paramName = parameterTypes[j].getName().replace('.', '/');
							mv.visitVarInsn(ALOAD, 3);
							pushConstInsn(mv, j);
							mv.visitInsn(AALOAD);
							castInsn(mv, paramName);
							unboxInsn(mv, paramName);
						}
					}

					mv.visitMethodInsn(INVOKEVIRTUAL, providerClassName, methodName, sigName, false);

					String returnTypeName = method.getReturnType().getName().replace('.', '/');
					if (returnTypeName.equals("void")) {
						mv.visitInsn(ACONST_NULL);
						mv.visitInsn(ARETURN);
					} else {
						boxInsn(mv, returnTypeName);
						mv.visitInsn(ARETURN);
					}

					++ sigIndex;
				}
				
				sigIndex = 0;
				++ methodIndex;
			}
		
			// final return null
			{
				mv.visitLabel(lastLabel);
				mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
				mv.visitInsn(ACONST_NULL);
				mv.visitInsn(ARETURN);
				mv.visitMaxs(4, 3);
				mv.visitEnd();
			}
		}
		cw.visitEnd();
		return (T)BinaryClassUtil.newInstance(cw.toByteArray(), provider);
	}
}
