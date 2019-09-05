package ru.vadimka.nfswlauncher;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;

public enum Locale {
	RU("ru","Русский"),
	EN("en","English");
	private String ID = null;
	private String NAME = null;
	
	private HashMap<String,String> STORAGE = new HashMap<String,String>();
	
	private Locale(String id, String name) {
		ID = id;
		NAME = name;
	}
	/**
	 * Получить локализацию по идентификатору
	 * @param id - Идентификатор языка
	 */
	public static Locale getLocaleById(String id) {
		switch (id.toLowerCase()) {
		case "ru":
			return Locale.RU;
		default:
			return Locale.EN;
		}
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
		Log.getLogger().warning("Ошибка локализации: "+alias);
		return "";
	}
	/**
	 * Загрузить строки из файла
	 */
	public void load() {
		InputStream is = Main.class.getResourceAsStream("resources/locales/"+ID+".cfg");
		if (is == null) {
			if (!ID.equalsIgnoreCase("en")) {
				ID = "en";
				Config.LANGUAGE = "en";
				Config.save();
				load();
				return;
			} else {
				Log.getLogger().warning("Ошибка при подгрузке локалей.");
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
				if (!str.substring(0, 1).equalsIgnoreCase("#") && !str.substring(0, 2).equalsIgnoreCase("//") && !str.contentEquals("")) {
					String val = strs[1];
					if (strs[1].length() > 0) {
						while(val.substring(val.length()-1).equalsIgnoreCase("\\")) {
							str = br.readLine();
							if (str == null) break;
							val = val.substring(0,val.length()-1)+str;
						}
					}
					STORAGE.put(strs[0], val);
				}
			}
			br.close();
			if (NAME == "" && STORAGE.containsKey("name")) NAME = STORAGE.get("name");
		} catch (Exception e) {
			Log.getLogger().warning("Ошибка чтения локалей");
		}
	}
}
