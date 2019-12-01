package ru.vadimka.nfswlauncher.utils;

import java.io.File;
import java.util.List;

import ru.vadimka.nfswlauncher.RWACIndexReader;

/**
 * Этот файл демонстрационный
 * Тело методов скрыто
 */
public abstract class RWAC {
	/**
	 * Проверка файлов
	 */
	public static boolean checkBeforeStart() {return false;}
	
	public static boolean checkFiles(List<RWACIndexReader.RWACFile> files) {return false;}
	/**
	 * Проверить файл на хэш
	 * @param file - файл, который нужно проверить
	 * @param CheckSums - список хэш
	 */
	public static boolean checkFile(File file, String[] CheckSums) {return false;}
	/**
	 * Добавить файл в индекс сохраненных файлов
	 * @param file
	 * @param CheckSumm
	 */
	public static void addBackubFileToIndex(String file, String CheckSumm) {}
}
