package ru.vadimka.nfswlauncher.locales;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;

import ru.vadimka.nfswlauncher.Config;
import ru.vadimka.nfswlauncher.Log;

public class Locale {
	private String ID = null;
	private String NAME = null;
	
	private HashMap<String,String> STORAGE = new HashMap<String,String>();
	
	public Locale(String id) {
		ID = id;
		NAME = "";
	}
	
	public Locale(String id, String name) {
		ID = id;
		NAME = name;
	}
	/**
	 * Идентификатор языка
	 * @return
	 */
	public String getID() {
		return ID;
	}
	/**
	 * Получить названия языка
	 * @return
	 */
	public String getName() {
		return NAME;
	}
	@Override
	public String toString() {
		return NAME;
	}
	/**
	 * Получить строку
	 * @param alias - идентификатор строки
	 * @return Переведенную строку
	 */
	public String get(String alias) {
		if (STORAGE.containsKey(alias)) return STORAGE.get(alias);
		Log.print("Ошибка локализации: "+alias);
		return "";
	}
	/**
	 * Загрузить строки из файла
	 */
	public void load() {
		InputStream is = Locale.class.getResourceAsStream(ID+".cfg");
		if (is == null) {
			if (!ID.equalsIgnoreCase("en")) {
				ID = "en";
				Config.LANGUAGE = "en";
				Config.save();
				load();
				return;
			} else {
				Log.print("Ошибка при подгрузке локалей.");
				return;
			}
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8"))); // is
		String str;
		String[] strs;
		try {
			while((str = br.readLine()) != null) {
				if (str.length() < 2) continue;
				strs = str.split(": ",2);
				if (!str.substring(0, 1).equalsIgnoreCase("#") && !str.substring(0, 2).equalsIgnoreCase("//"))
					STORAGE.put(strs[0], strs[1]);
			}
			br.close();
			if (NAME == "" && STORAGE.containsKey("name")) NAME = STORAGE.get("name");
		} catch (Exception e) {
			Log.print("Ошибка чтения локалей");
		}
	}
}
