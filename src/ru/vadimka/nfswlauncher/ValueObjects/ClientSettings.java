package ru.vadimka.nfswlauncher.ValueObjects;

import java.util.HashMap;

public class ClientSettings {
	/**
	 * Хранилище настроек
	 */
	private HashMap<String,String> STORAGE = new HashMap<String,String>();
	/**
	 * Контейнер настроек игры
	 * @param Language - Язык игры
	 * @param Audiomode - Тип звука 
	 * @param Audioquality - true звук будет чище
	 * @param Brightness - На 100 будет выглядеть радиактивно
	 * @param Performancelevel - Настройка графики
	 * @param Vsync - Включить версикальную синхронизацию
	 */
	public ClientSettings(String Language, boolean Audiomode, boolean Audioquality, int Brightness, int Performancelevel, boolean Vsync) {
		setLanguage(Language);
		setAudiomode(Audiomode);
		setAudioquality(Audioquality);
		setBrightness(Brightness);
		setPerformancelevel(Performancelevel);
		setVsync(Vsync);
	}
	/**
	 * Контейнер настроек игры
	 */
	public ClientSettings() {}
	/**
	 * Язык игры
	 */
	public String getLanguage() {
		if (STORAGE.containsKey("Language"))
			return STORAGE.get("Language");
		else
			return "RU";
	}
	/**
	 * Тип звука
	 */
	public boolean getAudiomode() {
		if (STORAGE.containsKey("audiomode") && STORAGE.get("audiomode").equalsIgnoreCase("1"))
			return true;
		else
			return false;
	}
	/**
	 * true звук будет чище
	 */
	public boolean getAudioquality() {
		if (STORAGE.containsKey("audioquality") && STORAGE.get("audioquality").equalsIgnoreCase("1"))
			return true;
		else
			return false;
	}
	/**
	 * На 100 будет выглядеть радиактивно
	 */
	public int getBrightness() {
		if (!STORAGE.containsKey("brightness")) return 52;
		return Integer.valueOf(STORAGE.get("brightness"));
	}
	/**
	 * Настройка графики
	 */
	public int getPerformancelevel() {
		if (!STORAGE.containsKey("performancelevel")) return 1;
		return Integer.valueOf(STORAGE.get("performancelevel"));
	}
	/**
	 * Включить версикальную синхронизацию
	 */
	public boolean getVsync() {
		if (STORAGE.containsKey("vsyncon") && STORAGE.get("vsyncon").equalsIgnoreCase("1"))
			return true;
		else
			return false;
	}
	/**
	 * Язык игры
	 */
	public void setLanguage(String s) {
		set("Language",s);
	}
	/**
	 * Тип звука
	 */
	public void setAudiomode(boolean b) {
		if (b)
			set("audiomode", "1");
		else
			set("audiomode", "0");
	}
	/**
	 * true звук будет чище
	 */
	public void setAudioquality(boolean b) {
		if (b)
			set("audioquality", "1");
		else
			set("audioquality", "0");
	}
	/**
	 * На 100 будет выглядеть радиактивно
	 */
	public void setBrightness(int i) {
		if (i < 0) i = 0;
		if (i > 100) i = 100;
		set("brightness", String.valueOf(i));
	}
	/**
	 * Настройка графики
	 */
	public void setPerformancelevel(int i) {
		if (i < 0) i = 0;
		if (i > 5) i = 5;
		set("performancelevel", String.valueOf(i));
	}
	/**
	 * Включить версикальную синхронизацию
	 */
	public void setVsync(boolean b) {
		if (b)
			set("vsyncon", "1");
		else
			set("vsyncon", "0");
	}
	/**
	 * Существет ли значение
	 * @param alias
	 * @return
	 */
	public boolean contents(String alias) {
		return STORAGE.containsKey(alias);
	}
	/**
	 * Получить значение
	 * @param alias - Псевдоним
	 */
	public String get(String alias) {
		return STORAGE.get(alias);
	}
	/**
	 * Записать значение
	 * @param alias - Псевдоним
	 * @param value - Значение
	 */
	public void set(String alias, String value) {
		if (STORAGE.containsKey(alias))
			STORAGE.replace(alias, value);
		else
			STORAGE.put(alias, value);
	}
}
