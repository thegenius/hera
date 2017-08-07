package com.lvonce.hera.asm;

import java.lang.reflect.Method;
import org.testng.annotations.Test;
import static org.testng.Assert.*;


public class SignatureUtilTest {

	public static interface SignatureTest {
		public void func1();
		public void func2(int a);
		public void func3(int[] a);
		public void func4(Integer[] a);
	}

	@Test
	public void test() {
		Method[] methods = SignatureTest.class.getDeclaredMethods();
		for (Method method: methods) {
			String sig = SignatureUtil.getSignature(method);
			System.out.println(method.getName());
			if (method.getName() == "func1") {
				assertEquals(sig, "()V");
				assertNull(SignatureUtil.getParamTypes(sig));
			}
			if (method.getName() == "func2") {
				assertEquals(sig, "(I)V");
				String[] paramTypes = SignatureUtil.getParamTypes(sig);
				assertEquals(paramTypes[0], "int");
			}
			if (method.getName() == "func3") {
				assertEquals(sig, "([I)V");
				String[] paramTypes = SignatureUtil.getParamTypes(sig);
				assertEquals(paramTypes[0], "[int");
			}
			if (method.getName() == "func4") {
				assertEquals(sig, "([Ljava/lang/Integer;)V");
				String[] paramTypes = SignatureUtil.getParamTypes(sig);
				assertEquals(paramTypes[0], "[java/lang/Integer");
			}
		}
	}
}

