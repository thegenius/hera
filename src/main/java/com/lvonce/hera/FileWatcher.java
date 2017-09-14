package com.lvonce.hera;
import com.lvonce.hera.logger.RpcLogger;

import java.util.Map;
import java.util.LinkedHashMap;

import java.io.IOException;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchKey;
import java.nio.file.WatchEvent;
import java.nio.file.WatchService;
import java.nio.file.FileSystems;
import static java.nio.file.StandardWatchEventKinds.*;

public class FileWatcher implements Runnable {

	private static boolean isRunning = false;
	private static WatchService watcher = null;
	private static FileWatcherHandler handler = null;
	public static void register(String filePathName, FileWatcherHandler handler) {
		try {
			if (watcher == null) {
				watcher = FileSystems.getDefault().newWatchService();
			}
			filePathName = new File(filePathName).getCanonicalPath();
			FileWatcher.handler = handler;	
			Paths.get(filePathName).register(
				watcher, 
				ENTRY_CREATE,
				ENTRY_DELETE,
				ENTRY_MODIFY);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void watch() {
		if (isRunning == false) {
			new Thread(new FileWatcher()).start();
			isRunning = true;
		}
	}

	@Override
	public void run() {
		try {
			while (true) {
				WatchKey key = watcher.take();
				for (WatchEvent<?> event : key.pollEvents()) {
					WatchEvent.Kind<?> kind = event.kind();
					if (kind == OVERFLOW) {
					}
				
					WatchEvent<Path> e = (WatchEvent<Path>)event;
					Path fileName = e.context();
					if (kind == ENTRY_CREATE) {
						File classFile = fileName.toFile();
						if (handler != null) {
							handler.updateByFile(classFile);
						}
						RpcLogger.info(getClass(), ""+fileName + " create");
					}	
					if (kind == ENTRY_DELETE) {
						RpcLogger.info(getClass(), ""+fileName + " delete");
					}	
					if (kind == ENTRY_MODIFY) {
						File classFile = fileName.toFile();
						if (handler != null && fileName.toString().endsWith(".groovy")) {
							handler.updateByFile(classFile);
						}
						//RpcLogger.info(getClass(), ""+fileName + " modify");
					}	
				}
				key.reset();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
