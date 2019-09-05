package ru.vadimka.nfswlauncher.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ru.vadimka.nfswlauncher.Config;
import ru.vadimka.nfswlauncher.Log;
import ru.vadimka.nfswlauncher.Main;
import ru.vadimka.nfswlauncher.ValueObjects.RWACFile;

public abstract class RWAC {
	
	public static boolean SomeErrors = false;
	
	public static void init() {}
	
	public static boolean isIndexDownloaded() {return true;}
	
	/**
	 * Проверка файлов
	 */
	public static boolean checkBeforeStart() {return true;}

	public static boolean checkFiles(byte[] xml) {return true;}
	/**
	 * Проверка файлов
	 * @param is - Входящий стрим
	 */
	public static boolean checkFiles(InputSource is) {return true;}
	/**
	 * Проверить файл на хэш
	 * @param file - файл, который нужно проверить
	 * @param CheckSums - список хэш
	 */
	public static boolean checkFile(File file, List<String> CheckSums) {return true;}
	/**
	 * Добавить файл в индекс сохраненных файлов
	 * @param file
	 * @param CheckSumm
	 */
	public static void addBackubFileToIndex(String file, String CheckSumm) {}
}
