package ru.vadimka.nfswlauncher.ValueObjects;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;

import ru.vadimka.nfswlauncher.Log;
import ru.vadimka.nfswlauncher.Main;
import ru.vadimka.nfswlauncher.utils.ByteUtils;
import ru.vadimka.nfswlauncher.utils.FileDownloader;
import ru.vadimka.nfswlauncher.utils.RWAC;

public class RWACFile {
	private FileDownloader fileDownloader;
	private String FilePath;
	private String FileName;
	private String[] CheckSums;
	
	public RWACFile(String donwloadLink, String fileName, String filePath, String[] checkSumms) {
		try {
			if (donwloadLink.equalsIgnoreCase("")) {
				fileDownloader = null;
			} else
				fileDownloader = new FileDownloader(donwloadLink);
		} catch (MalformedURLException e) {
			Log.getLogger().warning("Не верная ссылка на загрузку файла: "+filePath+File.pathSeparator+fileName);
			fileDownloader = null;
		}
		CheckSums = checkSumms;
		FileName = fileName;
		FilePath = filePath;
	}
	/**
	 * Добавить в задачи загрузку данного файла
	 */
	public boolean fix() {
		if (CheckSums.length < 1) {
			File file = new File(Main.getGameDir()+File.separator+getPath());
			File fileBackup = new File(Main.getGameDir()+File.separator+getPathBackgup());
			if (!file.canWrite()) {
				Log.getLogger().warning("[RWAC] Файл "+file+" помечен как нежелательный, но его не удалось удалить, так как отсутствуют права на запись.");
				return false;
			}
			try {
				FileDownloader.copyFile(file, fileBackup);
				file.delete();
			} catch (Exception e) {
				Log.getLogger().log(Level.WARNING,"[RWAC] Не удалось откопировать файл "+file.getAbsolutePath()+" в backup.",e);
				return false;
			}
			try {
				RWAC.addBackubFileToIndex(getPath(), ByteUtils.bytesToHexString(ByteUtils.getCheckSum(new FileInputStream(fileBackup)), true));
			} catch (Exception e) {
				Log.getLogger().log(Level.WARNING,"[RWAC] Не удалось добавить файл в index.",e);
				return false;
			}
			return true;
		}
		if (fileDownloader == null) {
			Log.getLogger().warning("[RWAC] Файл "+getPath()+" не может быть исправлен. Загрузите файл в ручную.");
			return false;
		}
		try {
			File outFile = new File(Main.getGameDir().getAbsolutePath()+File.separator+getPath());
			File filePath = Main.getGameDir();
			filePath = new File(filePath.getAbsolutePath()+File.separator+"RWAC");
			if (!filePath.exists()) filePath.mkdir();
			if (filePath.exists() && !filePath.isDirectory()) return false;
			filePath = new File(filePath.getAbsolutePath()+File.separator+Main.account.getServer().getName());
			if (!filePath.exists()) filePath.mkdir();
			File casheFile = new File(filePath.getAbsolutePath()+File.separator+getPath());
			if (!casheFile.exists() || (casheFile.exists() && !RWAC.checkFile(casheFile, CheckSums))) {
				if (!casheFile.getParentFile().exists()) casheFile.getParentFile().mkdirs();
				if (!casheFile.getParentFile().canWrite() || (casheFile.exists() && !casheFile.canWrite())) {
					Log.getLogger().warning("[RWAC] Внимание! Запись файла "+casheFile.getAbsolutePath()+" прервана, так как отсутствуют права на запись.");
					return false;
				}
				if (casheFile.exists()) casheFile.delete();
				fileDownloader.downloadAsFile(casheFile.getAbsolutePath());
				if (!RWAC.checkFile(casheFile, CheckSums)) {
					Log.getLogger().warning("[RWAC] Файл "+casheFile.getAbsolutePath()+" загрузился, но всеравно не соответствует требованиям.");
					return false;
				}
			}
			if (outFile.isDirectory()) outFile.delete();
			if (outFile.exists()) {
				File fileBackup = new File(Main.getGameDir()+File.separator+getPathBackgup());

				if (!fileBackup.exists()) {
					if (!fileBackup.getParentFile().exists()) fileBackup.getParentFile().mkdirs();
					FileDownloader.copyFile(outFile,fileBackup);
					RWAC.addBackubFileToIndex(getPath(), ByteUtils.bytesToHexString(ByteUtils.getCheckSum(new FileInputStream(fileBackup)), true));
				}
				outFile.delete();
			} else
				RWAC.addBackubFileToIndex(getPath(), "");
			File dir = outFile.getParentFile(); 
			if (!dir.exists() && !dir.mkdirs()) {
				Log.getLogger().warning("Не удалось создать директорию \""+outFile.getParentFile().getAbsolutePath()+"\"");
				return false;
			}
			if (!dir.canWrite() || (outFile.exists() && !outFile.canWrite())) {
				Log.getLogger().warning("[RWAC] Внимание! Запись файла "+outFile.getAbsolutePath()+" прервана, так как отсутствуют права на запись.");
				return false;
			}
			FileDownloader.copyFile(casheFile, outFile);
			if (!RWAC.checkFile(outFile, CheckSums)) {
				Log.getLogger().warning("[RWAC] Итоговый файл "+casheFile.getAbsolutePath()+" не удовлетворяет требованиям.");
				return false;
			}
		} catch (FileNotFoundException e) {
			Log.getLogger().log(Level.WARNING,"[RWAC] Не найден файл ",e);
			return false;
		} catch (IOException e) {
			Log.getLogger().log(Level.WARNING,"[RWAC] Ошибка ввода/вывода при загрузке файла",e);
			return false;
		} finally {
//			Log.getLogger().info("Файл \""+getPath()+"\" загружен");
		}
		return true;
	}
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
