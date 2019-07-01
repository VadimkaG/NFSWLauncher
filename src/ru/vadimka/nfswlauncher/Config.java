package ru.vadimka.nfswlauncher;

import java.io.File;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import ru.vadimka.nfswlauncher.utils.ConfigUtils;

public class Config {
	public static final String WINDOW_TITLE = "Racing World";
	public static final String VERSION = "0.11.S.9";
	public static final String UPDATE_INFO_URL = "https://raw.githubusercontent.com/VadimkaG/NFSWlauncher/master/version.txt";
	public static final String SERVERS_LIST_LINK = "https://raw.githubusercontent.com/VadimkaG/NFSWlauncher/master/server-list.xml";
	public static final boolean MODE_LOG_FILE = true;
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
		//USER_PASSWORD.charAt(5);
		ConfigUtils config = new ConfigUtils(Main.getWorkDir()+File.separator+"launcher.cfg");
		String str;
		
		str = config.getString("account");
		USER_LOGIN = "";
		USER_PASSWORD = "";
		if (str != null) {
			Decoder dec = Base64.getDecoder();
			String[] decoded = new String(dec.decode(str.getBytes())).split("\n");
			if (decoded != null && decoded.length == 2) {
				USER_LOGIN = decoded[0];
				USER_PASSWORD = decoded[1];
			}
		}
		
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
		
		if (USER_LOGIN != "" && USER_PASSWORD != "") {
			Encoder enc = Base64.getEncoder();
			String account = new String(enc.encode((USER_LOGIN+"\n"+USER_PASSWORD).getBytes()));
			config.set("account", account);
		}
		// Трем старые данные
		if (config.getString("login") != null)
			config.remove("login");
		if (config.getString("password") != null)
			config.remove("password");
		// ==================
		config.set("server_ip", SERVER_LINK);
		config.set("server_name", SERVER_NAME);
		config.set("server_protocol", SERVER_PROTOCOL);
		config.set("game_path", GAME_PATH);
		config.set("language", LANGUAGE);
		config.set("background_work", BACKGROUND_WORCK_DENY);
		config.save();
	}
	/**
	 * Усановать данные аккаунта
	 * @param login
	 * @param password
	 */
	public static void setAccount(String login, String password) {
		USER_LOGIN = login;
		USER_PASSWORD = password;
	}
	/**
	 * Установить данные сервера
	 * @param name - имя сервера
	 * @param ip - ip сервера
	 * @param protocol - протокол сервера
	 */
	public static void setServer(String name, String ip, String protocol) {
		SERVER_LINK = ip;
		SERVER_NAME = name;
		SERVER_PROTOCOL = protocol;
	}
}
