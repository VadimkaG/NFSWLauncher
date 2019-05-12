package ru.vadimka.nfswlauncher.utils;

import java.util.Stack;

public class AsyncTasksUtils {
	
	private static AsyncTasksUtils INSTANCE = null;
	
	public static AsyncTasksUtils call() {
		if (INSTANCE == null) {
			INSTANCE = new AsyncTasksUtils();
		}
		return INSTANCE;
	}
	
	private AsyncTasksUtils() {}
	
	private static final int MaxThreads = 10;
	
	private static int runningTasks = 0;
	
	private static Stack<AsyncTasksUtils.Task> tasks = new Stack<AsyncTasksUtils.Task>();
	
	private static void next() {
		if (runningTasks < MaxThreads) {
			if(tasks.isEmpty()) {
				if (runningTasks == 0) 
					synchronized (call()) {
						call().notifyAll();
					}
				return;
			}
			new Thread(tasks.pop()).start();
		}
	}
	
	public static void addTask(Runnable task) {
		tasks.add(call().new Task(task));
		next();
	}
	
	public static boolean inWork() {
		if (runningTasks > 0) return true;
		return false;
	}
	
	public class Task implements Runnable {
		
		private Runnable FUNC;
		
		public Task(Runnable func) {
			FUNC = func;
		}
		
		@Override
		public void run() {
			runningTasks++;
			FUNC.run();
			runningTasks--;
			next();
		}
		
	}
	
}
