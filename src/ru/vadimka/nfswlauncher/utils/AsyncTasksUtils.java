package ru.vadimka.nfswlauncher.utils;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Stack;

import ru.vadimka.nfswlauncher.Log;

public class AsyncTasksUtils {
	
	private static AsyncTasksUtils INSTANCE = null;
	
	private static int IdCounter = 0;
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
	private static HashMap<Integer,Task> runningTasks;
	
	private static Stack<AsyncTasksUtils.Task> tasks = new Stack<AsyncTasksUtils.Task>();
	
	protected static void next() {
		if (runningTasks == null) runningTasks = new HashMap<Integer,Task>();
		if (runningTasks.size() < MaxThreads) {
			synchronized (call()) {
				if(tasks.isEmpty()) {
					if (runningTasks.size() == 0) {
						IdCounter = 0;
						call().notifyAll();
					}
					return;
				} else {
						Task t = tasks.pop();
						IdCounter++;
						t.setId(IdCounter);
						runningTasks.put(IdCounter,t);
						t.start();
				}
			}
		} 
	}
	/**
	 * Подождать пока все задачи завершатся.
	 * @param seconds - Время ожидания в секундах
	 * @throws InterruptedException
	 */
	public static boolean waitTasks(int seconds) throws InterruptedException {
		if (!inWork()) return true;
		if (seconds > 0)
			Log.getLogger().info("Внимание! Загрузка может идти до "+seconds+" секунд.");
		else
			Log.getLogger().info("Внимание! На загрузку не ограничено время!");
		synchronized (call()) {
			if (seconds > 0) {
				call().wait(seconds*1000);
				if (runningTasks.size() > 0) {
					Log.getLogger().warning("Внимание! Прошло "+seconds+" секунд, но еще осталось "+runningTasks.size()+" задач.");
					Log.getLogger().warning("\tЗадачи: "+runningTasks);
					for (Entry<Integer, Task> ct : runningTasks.entrySet()) {
						Log.getLogger().warning("\tЗадача: "+ct.getValue());
						ct.getValue().getPagentThread().interrupt();
					}
					return false;
				}
			} else
				call().wait();
			return true;
		}
	}
	/**
	 * Подождать пока все задачи завершатся.
	 * @throws InterruptedException
	 */
	public static boolean waitTasks() throws InterruptedException {
		if (!inWork()) return true;
		synchronized (call()) {
			call().wait(5000);
			if (runningTasks.size() > 0) {
				Log.getLogger().warning("Внимание! Прошло 5-ть секунд, но еще осталось "+runningTasks.size()+" задач.");
				for (Entry<Integer, Task> ct : runningTasks.entrySet()) {
					Log.getLogger().warning("\tЗадача: "+ct.getValue());
					ct.getValue().getPagentThread().interrupt();
				}
				return false;
			}
			return true;
		}
	}
	/**
	 * Добавить новую задачу
	 * @param method - Метод задачи
	 */
	public static Task addTask(Runnable method) {
		Task task = new Task(method);
		tasks.add(task);
		next();
		return task;
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
	protected static class Task implements Runnable {
		
		public Runnable FUNC;
		private int ID;
		private Thread PARENT;

		public Task(Runnable func) {
			FUNC = func;
			PARENT = new Thread(this);
		}

		public void setId(int id) {
			ID = id;
		}

		public int getId() {
			return ID;
		}
		
		public void run() {
			FUNC.run();
			synchronized (call()) {
				runningTasks.remove(ID);
			}
			next();
		}
		
		public Thread getPagentThread() {
			return PARENT;
		}
		
		public String toString() {
			return "Task."+getId();
		}

		public void start() {
			PARENT.start();
		}
	}
}
