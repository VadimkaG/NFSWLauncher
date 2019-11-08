package ru.vadimka.nfswlauncher.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import ru.vadimka.nfswlauncher.Log;

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
	private static List<CustomTask> runningTasks;
	
	private static Stack<AsyncTasksUtils.Task> tasks = new Stack<AsyncTasksUtils.Task>();
	
	private static void next() {
		if (runningTasks == null) runningTasks = new ArrayList<CustomTask>();
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
				CustomTask ct= (CustomTask) t;
				ct.setParentThread(th);
				runningTasks.add(ct);
				th.start();
			}
		}
	}
	/**
	 * Подождать пока все задачи завершатся.
	 * @throws InterruptedException
	 */
	public static void waitTasks(int time) throws InterruptedException {
		if (!inWork()) return;
		Log.getLogger().info("Внимание! Загрузка может идти до "+time+" секунд.");
		synchronized (call()) {
			call().wait(time*1000);
			if (runningTasks.size() > 0) {
				Log.getLogger().warning("Внимание! Прошло 5-ть секунд, но еще осталось "+runningTasks.size()+" задач.");
				for (CustomTask ct : runningTasks) {
					ct.getPagentThread().interrupt();
				}
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
			call().wait(5000);
			if (runningTasks.size() > 0) {
				Log.getLogger().warning("Внимание! Прошло 5-ть секунд, но еще осталось "+runningTasks.size()+" задач.");
			}
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
	private interface CustomTask{
		public Thread getPagentThread();
		public void setParentThread(Thread parent);
	}
	/**
	 * Контроллер задач
	 */
	private static class Task implements CustomTask,Runnable {
		
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
		
		private Thread PARENT;
		
		@Override
		public Thread getPagentThread() {
			return PARENT;
		}

		@Override
		public void setParentThread(Thread parent) {
			PARENT = parent;
		}
		
	}
	/**
	 * Задача с параметрами
	 */
	public static abstract class ParamTask implements CustomTask,Runnable {
		
		public Object[] PARAMS;
		
		public ParamTask(Object[] params) {
			PARAMS = params.clone();
		}

		private Thread PARENT;
		@Override
		public Thread getPagentThread() {
			return PARENT;
		}

		@Override
		public void setParentThread(Thread parent) {
			PARENT = parent;
		}
		
	}
	
}
