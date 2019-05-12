package ru.vadimka.nfswlauncher;

import java.io.File;

import ru.vadimka.nfswlauncher.utils.ConfigUtils;

public class Config {
	public static final String WINDOW_TITLE = "Racing World";
	public static final String VERSION = "0.11_snapshot";
	public static final String UPDATE_INFO_URL = "https://raw.githubusercontent.com/VadimkaG/NFSWlauncher/master/version.txt";
	public static final String SERVERS_LIST_LINK = "https://raw.githubusercontent.com/VadimkaG/NFSWlauncher/master/server-list.xml";
	public static final boolean MODE_LOG = true;
	public static final boolean MODE_LOG_CONSOLE = true;
	public static String WINE_PATH;
	public static String WINE_PREFIX;
	public static String LANGUAGE;
	public static String UPDATE_NEW_VERSION;
	public static String UPDATE_LINK;
	public static String SERVER_LINK;
	public static String SERVER_NAME;
	public static String SERVER_PROTOCOL;
	public static String GAME_PATH;
	public static String USER_LOGIN;
	public static String USER_PASSWORD;
	public static boolean BACKGROUND_WORCK_DENY = false;
	/**
	 * Загрузить настройки из конфига
	 */
	public static void load() {
		ConfigUtils config = new ConfigUtils(Main.getWorkDir()+File.separator+"launcher.cfg");
		String str;
		
		// Загрузка логина
		str = config.getString("login");
		if (str == null) USER_LOGIN = "";
		else USER_LOGIN = str;
		
		// Загрузка пароля
		str = config.getString("password");
		if (str == null) USER_PASSWORD = "";
		else USER_PASSWORD = str;
		
		// Загрузка ссылки сервера
		str = config.getString("server_ip");
		if (str == null) SERVER_LINK = "";
		else SERVER_LINK = str;
		
		// Загрузка имени сервера
		str = config.getString("server_name");
		if (str == null) SERVER_NAME = "";
		else SERVER_NAME = str;
		
		// Загрузка протокола сервера
		str = config.getString("server_protocol");
		if (str == null) SERVER_PROTOCOL = "";
		else SERVER_PROTOCOL = str;
		
		// Загрузка пути в файлам игры
		str = config.getString("game_path");
		if (str == null) GAME_PATH = "";
		else GAME_PATH = str;
		
		// Загрузка языка в файлам игры
		str = config.getString("language");
		if (str == null) LANGUAGE = "ru";
		else LANGUAGE = str;
		
		BACKGROUND_WORCK_DENY = config.getBoolean("background_work");
		
		str = config.getString("winepath");
		if (str == null) WINE_PATH = "";
		else WINE_PATH = str;
		
		str = config.getString("wineprefix");
		if (str == null) WINE_PREFIX = "";
		else WINE_PREFIX = str;
	}
	/**
	 * Сохранить настройки в конфиг
	 */
	public static void save() {
		ConfigUtils config = new ConfigUtils(Main.getWorkDir()+File.separator+"launcher.cfg");
		config.set("login", USER_LOGIN);
		config.set("password", USER_PASSWORD);
		config.set("game_path", GAME_PATH);
		config.set("language", LANGUAGE);
		config.set("background_work", BACKGROUND_WORCK_DENY);
		config.save();
	}
	/**
	 * Сохранить сервер
	 */
	public static void saveServer(String name, String ip,String protocol) {
		ConfigUtils config = new ConfigUtils(Main.getWorkDir()+File.separator+"launcher.cfg");
		config.set("server_ip", ip);
		config.set("server_name", name);
		config.set("server_protocol", protocol);
		config.save();
	}
}
