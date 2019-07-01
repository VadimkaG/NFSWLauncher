package ru.vadimka.nfswlauncher.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.logging.Level;

import ru.vadimka.nfswlauncher.Config;
import ru.vadimka.nfswlauncher.Log;

public abstract class RWAC {
	/**
	 * Проверка файлов
	 */
	public static boolean checkBeforeStart() {
		return true;
	}

	public static boolean checkFiles(String xml) {
		return checkFiles(new InputSource(new StringReader(xml)));
	}
	/**
	 * Проверка файлов
	 * @param is - Входящий стрим
	 */
	public static boolean checkFiles(InputSource is) {
		return true;
	}
}
