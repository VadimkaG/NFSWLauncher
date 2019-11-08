package ru.vadimka.nfswlauncher;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import ru.vadimka.nfswlauncher.theme.LogWindow;

public abstract class Log {
	private static Logger LOGGER;
	
	public static void setLogger(Logger logger) {
		LOGGER = logger;
	}
	/**
	 * Преобразовать стэк путей в строку
	 * @param stacktrace - стэк пуей
	 * @return
	 */
	public static String stackTraceToString(Throwable th) {
		String str = "";
		str = stackTraceToString(th.getStackTrace());
		Throwable parent = th;
		String tab = "\t";
		while ((parent = parent.getCause()) != null) {
			str +=	"\n"+tab
					+"Причина: "+parent.toString()+"\n"
					+tab+"Описание: "+parent.getMessage()+"\n"
					+tab+"Использованный путь: \n";
			tab += "\t";
			str += stackTraceToString(parent.getStackTrace(),tab);
		}
		return str;
	}
	public static String stackTraceToString(StackTraceElement[] stacktrace) {
		return stackTraceToString(stacktrace,"\t");
	}
	public static String stackTraceToString(StackTraceElement[] stacktrace,String tab) {
		String str = tab;
		for (StackTraceElement element : stacktrace) {
			str += element.getClassName() + "."+element.getMethodName() + "(";
			if (element.getFileName() != null)
				str += element.getFileName()+":"+element.getLineNumber() + ")";
			else
				str += "Unknown Source)";
			str += "\n"+tab;
		}
		return str;
	}
	/**
	 * Получить объект логгера
	 * @return
	 */
	public static Logger getLogger() {
		return LOGGER;
	}
	public static String getLogFilePath() {
		/*DateFormat df = new SimpleDateFormat("dd-MM_HH-mm");
		Date today = Calendar.getInstance().getTime();
		String todayAsString = df.format(today);
		return Main.getWorkDir().getAbsolutePath()+"/rw_logs/"+todayAsString+".log";*/
		return Main.getConfigDir().getAbsolutePath()+"/launcher.log";
	}
	
	private static LogWindow logWindow = null;
	
	public static void showLogWindow() {
		if (logWindow == null) {
			logWindow = new LogWindow().setDescription("Служебное окно для поиска неисправностей.");
			Log.getLogger().addHandler(new LogWindow.LogWindowHandler());
		}
		if (!logWindow.isVisible()) logWindow.setVisible(true);
	}
	
	public static class LogHandler extends Handler {
		@Override
		public void close() throws SecurityException {}
		@Override
		public void flush() {}
		@Override
		public void publish(LogRecord record) {
			DateFormat df = new SimpleDateFormat("dd.MM HH:mm");
			Date today = Calendar.getInstance().getTime();
			String todayAsString = df.format(today);
			String message = "["+todayAsString +" | "+record.getLevel()+"] " + record.getMessage();
			Throwable th;
			if ((th = record.getThrown()) != null) {
				message += ":\n=============Подробное описание=============";
				message += "\nКласс: "+th.getClass().getCanonicalName();
				message += "\nОписание: "+th.getMessage();
				message += "\nПричина: "+th.getCause();
				message += "\nИспользованный путь:\n"+stackTraceToString(th.getStackTrace());
				message += "\n=============================================";
			}
			print(message);
		}
		
		public void print(String message) {
			System.out.println(message);
		}
		
	}
	public static class FLogHandler extends LogHandler {
		
		private File FILE;
		
		public FLogHandler(String file) {
			FILE = new File(Log.getLogFilePath());
			if (FILE.exists()) FILE.delete();
			if (!FILE.exists() && !FILE.getParentFile().exists()) FILE.getParentFile().mkdirs();
		}
		@Override
		public void print(String message) {
			if (FILE.exists() && !FILE.canWrite()) return;
			try {
				String str = "";
				if (FILE.exists()) {
					StringBuilder response;
					try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(FILE)))) {
						response = new StringBuilder();
						String line;
						while ((line = br.readLine()) != null) {
							response.append(line);
							response.append("\n");
						}
						str = response.toString();
						br.close();
					}
				}
				DataOutputStream dos = new DataOutputStream(new FileOutputStream(FILE));
				str = str + message;
				byte[] data = str.getBytes();
				dos.write(data);
				dos.close();
			} catch (Exception e) {
				System.out.println("Ошибка: "+e.getMessage());
			}
		}
	}
}
