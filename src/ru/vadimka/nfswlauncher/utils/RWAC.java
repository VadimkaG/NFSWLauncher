package ru.vadimka.nfswlauncher.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ru.vadimka.nfswlauncher.Config;
import ru.vadimka.nfswlauncher.Log;
import ru.vadimka.nfswlauncher.locales.Locale;

public abstract class RWAC {
	/**
	 * Инициальзация
	 */
	public static void load() {}
	/**
	 * Проверка файлов
	 */
	public static boolean checkBeforeStart() {}
	/**
	 * Преобразовать строку HEX в массив байтов
	 * В строке должны быть HEX байты, без запятых и пробелов
	 * @param str Строка, в которой HEX символы
	 * @return byte[]
	 */
	private static byte[] hexStringToBytes(String str) {}
}
