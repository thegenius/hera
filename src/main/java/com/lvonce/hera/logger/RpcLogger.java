package com.lvonce.hera.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.management.ManagementFactory;

public class RpcLogger {
	public static void info(Class classType, String msg) {
		msg = ManagementFactory.getRuntimeMXBean().getName() + ":" + msg;
		LoggerFactory.getLogger(classType).info(msg);
	}
	public static void info(Class classType, String msg, Object ...args) {
		msg = ManagementFactory.getRuntimeMXBean().getName() + ":" + msg;
		LoggerFactory.getLogger(classType).info(msg, args);
	}
	public static void debug(Class classType, String msg) {
		msg = ManagementFactory.getRuntimeMXBean().getName() + ":" + msg;
		LoggerFactory.getLogger(classType).debug(msg);
	}
	public static void debug(Class classType, String msg, Object ...args) {
		msg = ManagementFactory.getRuntimeMXBean().getName() + ":" + msg;
		LoggerFactory.getLogger(classType).debug(msg, args);
	}
	public static void warn(Class classType, String msg) {
		msg = ManagementFactory.getRuntimeMXBean().getName() + ":" + msg;
		LoggerFactory.getLogger(classType).warn(msg);
	}
	public static void warn(Class classType, String msg, Object ...args) {
		msg = ManagementFactory.getRuntimeMXBean().getName() + ":" + msg;
		LoggerFactory.getLogger(classType).warn(msg, args);
	}
}
