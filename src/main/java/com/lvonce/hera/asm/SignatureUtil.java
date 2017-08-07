package com.lvonce.hera.asm;

import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.lang.Integer;
import java.lang.reflect.Method;

public class SignatureUtil {
	private static final LinkedHashMap<String, String> sigStringMap = new LinkedHashMap<String, String>(){{
		put("boolean", 	"Z");
		put("byte", 	"B");
		put("char", 	"C");
		put("double", 	"D");
		put("float",	"F");
		put("int", 		"I");
		put("long", 	"J");
		put("Object", 	"L");
		put("short", 	"S");
		put("void", 	"V");
		put("array", 	"[");
	}};
	
	private static final LinkedHashMap<Character, String> sigClassMap = new LinkedHashMap<Character, String>(){{
		put('Z', "boolean");
		put('B', "byte");
		put('C', "char");
		put('D', "double");
		put('F', "float");
		put('I', "int");
		put('J', "long");
		put('L', "Object");
		put('S', "short");
		put('V', "void");
		put('[', "array");
	}};

	
	public static String getTypeString(String sig, int[] indexWrapper) {	
		int index = indexWrapper[0];
		if (sig.charAt(index) == '[') {
			++ index;
			if (sig.charAt(index) == '[') {
				++ index;
				indexWrapper[0] = index;	
				return "[["+getTypeString(sig, indexWrapper);	
			}
			indexWrapper[0] = index;	
			return "["+getTypeString(sig, indexWrapper);	
		}

		if (sig.charAt(index) == 'L') {
			StringBuilder builder = new StringBuilder();
			++ index;
			while (sig.charAt(index) != ';') {
				builder.append(sig.charAt(index));
				++ index;
			}
			++ index;
			indexWrapper[0] = index;
			return builder.toString().replace('.', '/');
		}

		String result = sigClassMap.getOrDefault(sig.charAt(index), "");
		++ index;
		indexWrapper[0] = index;
		return result;
	}

	public static String[] getParamTypes(String sig) {
    	ArrayList<String> parameterList = new ArrayList<String>();
	
		Integer index = 0;
		int len = sig.length();
		while (sig.charAt(index) != '(') {
			++ index;
		}
		++ index;
		while (sig.charAt(index) != ')') {
			int[] indexWrapper = {index};
			String typeString = getTypeString(sig, indexWrapper);
			parameterList.add(typeString);
			index = indexWrapper[0];
		}

		if (parameterList.size() != 0) {
			String[] parameterTypes = new String[parameterList.size()];
			parameterList.toArray(parameterTypes);
			return parameterTypes;
		} else {
			return null;
		}
  	}

	public static String getTypeSig(Class<?> classType) {
		String typeName = classType.getName();
		typeName = typeName.replace('.', '/');
		if (typeName.startsWith("[")) {
			return sigStringMap.getOrDefault(typeName, typeName);
		} else {
			return sigStringMap.getOrDefault(typeName, "L"+typeName+";");
		}
	}

  	public static String getSignature (Method method) {
		return getSignature(method, null);
	}

  	public static String getSignatureWithoutReturnType (Method method) {
    	Class<?>[] parameterTypes = method.getParameterTypes();
    	StringBuilder paramString = new StringBuilder();
    	paramString.append("(");
    	for (Class<?> paramType : parameterTypes) {
			String typeName = paramType.getName();
			typeName = typeName.replace('.', '/');
			if (typeName.startsWith("[")) {
				String sig = sigStringMap.getOrDefault(typeName, typeName);
				paramString.append(sig);
			} else {
				String sig = sigStringMap.getOrDefault(typeName, "L"+typeName+";");
				paramString.append(sig);
			}
    	}
    	paramString.append(")");
    	return paramString.toString();
  	}

  	public static String getSignature (Method method, Class<?> returnClass) {
    	Class<?>[] parameterTypes = method.getParameterTypes();
    	StringBuilder paramString = new StringBuilder();
    	paramString.append("(");
    	for (Class<?> paramType : parameterTypes) {
			String typeName = paramType.getName();
			typeName = typeName.replace('.', '/');
			if (typeName.startsWith("[")) {
				String sig = sigStringMap.getOrDefault(typeName, typeName);
				paramString.append(sig);
			} else {
				String sig = sigStringMap.getOrDefault(typeName, "L"+typeName+";");
				paramString.append(sig);
			}
    	}
    	paramString.append(")");

		Class<?> returnType = method.getReturnType();
		if (returnClass != null) {
			returnType = returnClass;
		} 
		String returnTypeName = returnType.getName().replace('.', '/');
		if (returnTypeName.startsWith("[")) {
			paramString.append(sigStringMap.getOrDefault(returnTypeName, returnTypeName));
		} else {
			paramString.append(sigStringMap.getOrDefault(returnTypeName, "L"+returnTypeName+";"));
		}
    	return paramString.toString();
  	}
}
