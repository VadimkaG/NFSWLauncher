package ru.vadimka.nfswlauncher.ValueObjects;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Level;

import ru.vadimka.nfswlauncher.Log;
import ru.vadimka.nfswlauncher.Main;
import ru.vadimka.nfswlauncher.utils.AsyncTasksUtils;
import ru.vadimka.nfswlauncher.utils.CheckSum;
import ru.vadimka.nfswlauncher.utils.FileDownloader;
import ru.vadimka.nfswlauncher.utils.RWAC;

public class RWACFile {
	private FileDownloader fileDownloader;
	private String FilePath;
	private String FileName;
	private List<String> CheckSums;
	protected static int filesInProgress = 0;
	
	public RWACFile(String donwloadLink, String fileName, String filePath, List<String> checkSumms) {}
	/**
	 * Добавить в задачи загрузку данного файла
	 */
	public void fix() {}
	/**
	 * Дождаться всех задач
	 */
	public static void waitAllTasks() {}
	@Override
	public String toString() {
		return getPath();
	}
	/**
	 * Получить путь к файлу
	 * @return
	 */
	public String getPath() {
		return FilePath+FileName;
	}
	/**
	 * Получить путь к файлу в папке бекапа стандартных файлов
	 * @return
	 */
	public String getPathBackgup() {
		return getDefaultPath()+getPath();
	}
	/**
	 * Получить папку с бекапами стандартных файлов NFSW
	 * @return
	 */
	public static String getDefaultPath() {
		return "RWAC"+File.separator+"backup"+File.separator;
	}
}
