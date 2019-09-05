package ru.vadimka.nfswlauncher.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class AsyncTasksUtils {
	
	private static AsyncTasksUtils INSTANCE = null;
	/**
	 * Получить экземпляр ассинхронного менеджера
	 * @return
	 */
	private static AsyncTasksUtils call() {
		if (INSTANCE == null) {
			INSTANCE = new AsyncTasksUtils();
		}
		return INSTANCE;
	}
	
	private AsyncTasksUtils() {}
	
	private static final int MaxThreads = 20;
	
	private static List<Runnable> runningTasks;
	
	private static Stack<AsyncTasksUtils.Task> tasks = new Stack<AsyncTasksUtils.Task>();
	
	private static void next() {
		if (runningTasks == null) runningTasks = new ArrayList<Runnable>();
		if (runningTasks.size() < MaxThreads) {
			if(tasks.isEmpty()) {
				if (runningTasks.size() == 0) 
					synchronized (call()) {
						call().notifyAll();
					}
				return;
			} else {
				Runnable t = tasks.pop();
				Thread th = new Thread(t);
				runningTasks.add(t);
				th.start();
			}
		}
	}
	/**
	 * Подождать пока все задачи завершатся.
	 * @throws InterruptedException
	 */
	public static void waitTasks() throws InterruptedException {
		if (!inWork()) return;
		synchronized (call()) {
			call().wait();
		}
	}
	/**
	 * Добавить новую задачу
	 * @param task - Метод задачи
	 */
	public static void addTask(Runnable task) {
		tasks.add(new Task(task));
		next();
	}
	/**
	 * Остались ли задачи в работе
	 * @return
	 */
	public static boolean inWork() {
		if (runningTasks.size() > 0) return true;
		return false;
	}
	/**
	 * Контроллер задач
	 */
	private static class Task implements Runnable {
		
		public Runnable FUNC;
		
		public Task(Runnable func) {
			FUNC = func;
		}
		
		@Override
		public void run() {
			FUNC.run();
			runningTasks.remove(this);
			next();
		}
		
	}
	/**
	 * Задача с параметрами
	 */
	public static abstract class ParamTask implements Runnable {
		
		public Object[] PARAMS;
		
		public ParamTask(Object[] params) {
			PARAMS = params.clone();
		}
		
	}
	
}
