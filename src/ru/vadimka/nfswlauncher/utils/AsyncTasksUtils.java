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
	private static HashMap<Integer,CustomTask> runningTasks;
	
	private static Stack<AsyncTasksUtils.Task> tasks = new Stack<AsyncTasksUtils.Task>();
	
	private static void next() {
		if (runningTasks == null) runningTasks = new HashMap<Integer,CustomTask>();
		if (runningTasks.size() < MaxThreads) {
			synchronized (call()) {
				if(tasks.isEmpty()) {
					if (runningTasks.size() == 0) {
						IdCounter = 0;
						call().notifyAll();
					}
					return;
				} else {
						Runnable t = tasks.pop();
						Thread th = new Thread(t);
						CustomTask ct= (CustomTask) t;
						IdCounter++;
						ct.setId(IdCounter);
						ct.setParentThread(th);
						runningTasks.put(Integer.valueOf(IdCounter),ct);
						th.start();
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
					for (Entry<Integer, CustomTask> ct : runningTasks.entrySet()) {
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
				for (Entry<Integer, CustomTask> ct : runningTasks.entrySet()) {
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
	 * @param task - Метод задачи
	 */
	public static void addTask(Runnable task, String name) {
		tasks.add(new Task(task, name));
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
		String getName();
		int getId();
		void setId(int id);
		Thread getPagentThread();
		void setParentThread(Thread parent);
	}
	/**
	 * Контроллер задач
	 */
	private static class Task implements CustomTask,Runnable {
		
		public Runnable FUNC;
		private String NAME;
		private int ID;
		
		public Task(Runnable func, String name) {
			FUNC = func;
			NAME = name;
		}
		
		@Override
		public void setId(int id) {
			ID = id;
		}

		@Override
		public int getId() {
			return ID;
		}

		@Override
		public String getName() {
			return NAME;
		}
		
		@Override
		public void run() {
			FUNC.run();
			synchronized (call()) {
				runningTasks.remove(ID);
			}
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
		
		@Override
		public String toString() {
			return "["+getId()+"] "+getName();
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
