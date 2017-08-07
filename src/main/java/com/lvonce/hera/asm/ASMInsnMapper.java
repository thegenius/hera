package com.lvonce.hera.asm;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.lang.reflect.Method;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Label;
import static org.objectweb.asm.Opcodes.*;

public class ASMInsnMapper {
	
	public static void pushConstInsn(MethodVisitor mv, int value) {
		switch (value) {
			case -1: mv.visitInsn(ICONST_M1);
			case 0: mv.visitInsn(ICONST_0);return;
			case 1: mv.visitInsn(ICONST_1);return;
			case 2: mv.visitInsn(ICONST_2);return;
			case 3: mv.visitInsn(ICONST_3);return;
			case 4: mv.visitInsn(ICONST_4);return;
			case 5: mv.visitInsn(ICONST_5);return;
		}
		if (value >= -128 && value <= 127) {
			mv.visitIntInsn(BIPUSH, value);
			return;
		}
		if (value >= -32768 && value <= 32767) {
			mv.visitIntInsn(SIPUSH, value);
			return;
		}
		if (value >= -2147483648 && value <= 2147483647) {
			mv.visitIntInsn(LDC, value);
			return;
		}	
	}

	public static void returnInsn(MethodVisitor mv, String returnTypeName) {
		switch (returnTypeName) {
			case "void": 
				mv.visitInsn(POP);
				mv.visitInsn(RETURN);
				return;
			case "boolean":
				mv.visitInsn(IRETURN);
				return;
			case "byte":
				mv.visitInsn(IRETURN);
				return;
			case "char":
				mv.visitInsn(IRETURN);
				return;
			case "short":
				mv.visitInsn(IRETURN);
				return;
			case "int": ;
				mv.visitInsn(IRETURN);
				return;
			case "long": 
				mv.visitInsn(LRETURN);
				return;
			case "float": 
				mv.visitInsn(FRETURN);
				return;
			case "double": 
				mv.visitInsn(DRETURN);
				return;
			default: 
				mv.visitInsn(ARETURN);
				return;
		}	
	}
	
	public static void loadInsn(MethodVisitor mv, String typeName, int index) {
		switch (typeName) {
			case "boolean":
				mv.visitVarInsn(ILOAD, index);
				return;
			case "byte":
				mv.visitVarInsn(ILOAD, index);
				return;
			case "char":
				mv.visitVarInsn(ILOAD, index);
				return;
			case "short":
				mv.visitVarInsn(ILOAD, index);
				return;
			case "int": 
				mv.visitVarInsn(ILOAD, index);
				return;
			case "long": 
				mv.visitVarInsn(LLOAD, index);
				return;
			case "float": 
				mv.visitVarInsn(FLOAD, index);
				return;
			case "double": 
				mv.visitVarInsn(DLOAD, index);
				return;
			default: 
				mv.visitVarInsn(ALOAD, index);
				return;
		}	
	}
	
	public static void boxInsn(MethodVisitor mv, String typeName) {
		switch (typeName) {
			case "boolean":
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(I)Ljava/lang/Boolean;", false);
				return;
			case "byte":
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(I)Ljava/lang/Byte;", false);
				return;
			case "char":
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(I)Ljava/lang/Character;", false);
				return;
			case "short":
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(I)Ljava/lang/Short;", false);
				return;
			case "int": 
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				return;
			case "long": 
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(I)Ljava/lang/Long;", false);
				return;
			case "float": 
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(I)Ljava/lang/Float;", false);
				return;
			case "double": 
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(I)Ljava/lang/Double;", false);
				return;
			default: 
				return;
		}	
	}
	
	public static void unboxInsn(MethodVisitor mv, String typeName) {
		switch (typeName) {
			case "boolean":
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()I", false);
				return;
			case "byte":
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()I", false);
				return;
			case "char":
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()I", false);
				return;
			case "short":
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()I", false);
				return;
			case "int": ;
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
				return;
			case "long": 
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()I", false);
				return;
			case "float": 
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()I", false);
				return;
			case "double": 
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/double", "doubleValue", "()I", false);
				return;
			default: 
				return;
		}	
	}
	
	public static void castInsn(MethodVisitor mv, String typeName) {
		switch (typeName) {
			case "boolean":
				mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
				return;
			case "byte":
				mv.visitTypeInsn(CHECKCAST, "java/lang/Byte");
				return;
			case "char":
				mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
				return;
			case "short":
				mv.visitTypeInsn(CHECKCAST, "java/lang/Short");
				return;
			case "int": ;
				mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
				return;
			case "long": 
				mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
				return;
			case "float": 
				mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
				return;
			case "double": 
				mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
				return;
			default: 
				mv.visitTypeInsn(CHECKCAST, typeName);
				return;
		}
	}

}
