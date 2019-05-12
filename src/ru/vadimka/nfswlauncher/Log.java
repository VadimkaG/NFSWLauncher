package ru.vadimka.nfswlauncher;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public abstract class Log {
	private static Log INSTANCE;
	public static Log get() {return INSTANCE;}
	private static File logFile;
	public static void init() {
		Main.getWorkDir();
		logFile = new File(Main.getWorkDir().getAbsolutePath()+"/launcher.log");
		if (logFile.exists()) {
			logFile.delete();
		}
	}
	public static void print(String message) {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date today = Calendar.getInstance().getTime();
		String todayAsString = df.format(today);
		message = "["+todayAsString +"] " + message;
		if (Config.MODE_LOG) {
			try {
				String str = "";
				if (logFile.exists()) {
					InputStream is = new FileInputStream(logFile);
					StringBuilder response;
					try (BufferedReader rd = new BufferedReader(new InputStreamReader(is))) {
						response = new StringBuilder();
						String line;
						while ((line = rd.readLine()) != null) {
							response.append(line);
							response.append("\n");
						}
						str = response.toString();
					}
				}
				FileOutputStream fos = new FileOutputStream(logFile);
				DataOutputStream dos = new DataOutputStream(fos);
				str = str + message;
				byte[] data = str.getBytes();
				dos.write(data);
				dos.close();
				fos.close();
			} catch (Exception e) {}
		}
		if (Config.MODE_LOG_CONSOLE)
			System.out.println(message);
	}
	public static void print(StackTraceElement[] stacktrace) {
		String str = "Log.StackTrace: ";
		for (StackTraceElement element : stacktrace) {
			str += element.getClassName() + "."+element.getMethodName() + "(";
			if (element.getFileName() != null)
				str += element.getFileName()+":"+element.getLineNumber() + ")";
			else
				str += "Unknown Source)";
			str += "   ";
		}
		Log.print(str);
	}
}
