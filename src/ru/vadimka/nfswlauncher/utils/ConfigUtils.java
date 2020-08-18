package ru.vadimka.nfswlauncher.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigUtils {
	private File file;
	private HashMap<String,String> storage;
	/**
	 * Файл конфига
	 * Конфиг считывает данные конфига при инициализации.
	 * Конфиг автоматически записывается, при измении значения.
	 * @param filePath - Путь к файлу.
	 */
	public ConfigUtils(String filePath) {
		file = new File(filePath);
		storage = new HashMap<String,String>();
		load();
	}
	/**
	 * Сохранить конфиг
	 */
	public void save() {
		if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
		if (file.isDirectory()) return;
		if (file.exists() && !file.canWrite()) return;
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			for (Map.Entry<String, String> element : storage.entrySet()) {
				bw.write(element.getKey()+": "+element.getValue()+"\n");
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Прочитать конфиг
	 */
	private void load() {
		storage.clear();
		if (!file.exists()) return;
		if (file.isDirectory()) return;
		if (!file.canRead()) return;
		BufferedReader br;
		String[] strs;
		try {
			br = new BufferedReader(new FileReader(file));
			String str;
			while((str = br.readLine()) != null) {
				strs = str.split(": ",2);
				
				if (str.length() != 0 && strs.length > 1 && !str.substring(0, 1).equalsIgnoreCase("#") && !str.substring(0, 2).equalsIgnoreCase("//") && !str.contentEquals("")) {
					storage.put(strs[0], strs[1]);
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Получить значение из загруженного конфига
	 * @param index - Индекс значения
	 * @return Строковое значение
	 */
	public String getString(String index) {
		if (storage.containsKey(index)) {
			return storage.get(index);
		} else return "";
	}
	/**
	 * Установить значение
	 * @param index - Индекс занчения
	 * @param value - Строковое значение
	 */
	public void set(String index, String value) {
		if (storage.containsKey(index)) {
			storage.replace(index, value);
		} else {
			storage.put(index, value);
		}
		//save();
	}
	/**
	 * Установить значение
	 * @param index - Индекс занчения
	 * @param value - Числовое значение
	 */
	public void set(String index, int value) {
		set(index,String.valueOf(value));
	}
	/**
	 * Установить значение
	 * @param index - Индекс занчения
	 * @param value - Логическое значение
	 */
	public void set(String index, boolean value) {
		set(index,String.valueOf(value));
	}
	/**
	 * Установить значение
	 * @param index - Индекс занчения
	 * @param value - Доробное значение
	 */
	public void set(String index, double value) {
		set(index,String.valueOf(value));
	}/**
	 * Получить значение из загруженного конфига
	 * @param index - Индекс значения
	 * @return String - Логическое значение
	 */
	public boolean getBoolean(String index) {
		if (getString(index).equalsIgnoreCase("true")) return true;
		else return false;
	}
	/**
	 * Получить значение из загруженного конфига
	 * @param index - Индекс значения
	 * @param def - Значение по умолчанию
	 * @return String - Логическое значение
	 */
	public boolean getBoolean(String index, boolean def) {
		if (getString(index).equalsIgnoreCase("true")) return true;
		else if (getString(index).equalsIgnoreCase("false")) return false;
		return def;
	}
	/**
	 * Получить значение из загруженного конфига
	 * @param index - Индекс значения
	 * @return Целое число
	 */
	public int getInteger(String index) {
		if (getString(index).equalsIgnoreCase("")) return 0;
		else return Integer.parseInt(getString(index));
	}
	/**
	 * Получить значение из загруженного конфига
	 * @param index - Индекс значения
	 * @return Логическое значение
	 */
	public double getDouble(String index) {
		if (getString(index).equalsIgnoreCase("")) return 0.0;
		else return Double.parseDouble(getString(index));
	}
	/**
	 * Удалить значение из базы
	 * @param index - Индекс значения
	 */
	public void remove(String index) {
		if (storage.containsKey(index))
			storage.remove(index);
	}
}