package ru.vadimka.nfswlauncher;

import java.io.File;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ru.vadimka.nfswlauncher.utils.ConfigUtils;

public abstract class Config {
	public static final String WINDOW_TITLE = "Racing World";
	public static final String VERSION = "0.15.4.2";
	public static final String UPDATE_INFO_URL = "https://raw.githubusercontent.com/VadimkaG/NFSWlauncher/master/version.txt";
	public static final String SERVERS_LIST_LINK = "https://raw.githubusercontent.com/VadimkaG/NFSWlauncher/master/server-list.xml";
	public static final boolean MODE_LOG_FILE = true;
	public static final boolean MODE_LOG_CONSOLE = false;
	public static boolean DISCORD_ALLOW = true;
	public static String WINE_PATH;
	public static String WINE_PREFIX;
	public static String LANGUAGE;
	public static String UPDATE_NEW_VERSION;
	public static String UPDATE_LINK;
	public static String SERVER_LINK;
	public static boolean SERVER_HTTPS;
	public static String SERVER_NAME;
	public static String SERVER_PROTOCOL;
	public static String GAME_PATH;
	public static String USER_LOGIN;
	public static String USER_PASSWORD;
	public static boolean BACKGROUND_WORCK_DENY = false;
	public static boolean IS_UPDATE_CHECK = true;
	public static boolean IS_DYNAMIC_BACKGROUND = false;
	
	public static boolean USE_REDIRECT = false;
	/**
	 * Загрузить настройки из конфига
	 */
	public static void load() {
		ConfigUtils config = new ConfigUtils(Main.getConfigDir()+File.separator+"launcher.cfg");
		String str;
		
		str = config.getString("account");
		USER_LOGIN = "";
		USER_PASSWORD = "";
		if (str != null) {
			String[] decoded;
			try {
				Decoder dec = Base64.getDecoder();
				decoded = new String(dec.decode(str.getBytes())).split("\n");
			} catch (java.lang.IllegalArgumentException e) {
				decoded = new String(str.getBytes()).split("\t");
			}
			if (decoded != null && decoded.length == 2) {
				USER_LOGIN = decoded[0];
				USER_PASSWORD = decoded[1];
			}
		}
		
		// Загрузка сервера
		str = config.getString("server");
		if (str == null) {
			SERVER_LINK = "";
			SERVER_NAME = "";
			SERVER_PROTOCOL = "";
			SERVER_HTTPS = false;
		} else {
			try {
				JSONObject jo = (JSONObject) new JSONParser().parse(str);
				
				if (jo.containsKey("l"))
					SERVER_LINK =  (String)jo.get("l");
				if (jo.containsKey("n"))
					SERVER_NAME =  (String)jo.get("n");
				if (jo.containsKey("p"))
					SERVER_PROTOCOL =  (String)jo.get("p");
				if (jo.containsKey("h"))
					SERVER_HTTPS =  (boolean)jo.get("h");
			} catch (ParseException e) {
				SERVER_LINK = "";
				SERVER_NAME = "";
				SERVER_PROTOCOL = "";
				SERVER_HTTPS = false;
			}
		}
		
		// Загрузка пути в файлам игры
		str = config.getString("game_path");
		if (str == null) GAME_PATH = "";
		else GAME_PATH = str;
		
		// Загрузка языка в файлам игры
		str = config.getString("language");
		if (str == null) LANGUAGE = "ru";
		else LANGUAGE = str;
		
		BACKGROUND_WORCK_DENY = config.getBoolean("background_work",BACKGROUND_WORCK_DENY);
		
		IS_UPDATE_CHECK = config.getBoolean("is_update_check",IS_UPDATE_CHECK);
		
		IS_DYNAMIC_BACKGROUND = config.getBoolean("is_dynamic_background",IS_DYNAMIC_BACKGROUND);
		
		str = config.getString("winepath");
		if (str == null) WINE_PATH = "";
		else WINE_PATH = str;
		
		str = config.getString("wineprefix");
		if (str == null) WINE_PREFIX = "";
		else WINE_PREFIX = str;
		
		if (config.getString("discord_allow") != "")
			DISCORD_ALLOW = config.getBoolean("discord_allow");
	}
	/**
	 * Сохранить настройки в конфиг
	 */
	@SuppressWarnings("unchecked")
	public static void save() {
		ConfigUtils config = new ConfigUtils(Main.getConfigDir()+File.separator+"launcher.cfg");
		
		if (USER_LOGIN != "" && USER_PASSWORD != "") {
			Encoder enc = Base64.getEncoder();
			String account = new String(enc.encode((USER_LOGIN+"\n"+USER_PASSWORD).getBytes()));
			config.set("account", account);
		}
		
		// Трем старые данные, которые в новой версии не поддерживаются
		if (config.getString("login") != null)
			config.remove("login");
		if (config.getString("password") != null)
			config.remove("password");
		if (config.getString("server_ip") != null)
			config.remove("server_ip");
		if (config.getString("server_protocol") != null)
			config.remove("server_protocol");
		if (config.getString("server_name") != null)
			config.remove("server_name");
		// ==================
		
		JSONObject server = new JSONObject();
		server.put("l", SERVER_LINK);
		server.put("n", SERVER_NAME);
		server.put("p", SERVER_PROTOCOL);
		server.put("h", SERVER_HTTPS);
		config.set("server", server.toJSONString());
		
		config.set("game_path", GAME_PATH);
		config.set("language", LANGUAGE);
		config.set("background_work", BACKGROUND_WORCK_DENY);
		config.set("is_update_check", IS_UPDATE_CHECK);
		config.set("is_dynamic_background", IS_DYNAMIC_BACKGROUND);
		config.set("discord_allow", DISCORD_ALLOW);
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
	/**
	 * Сохранить конфигурацию WINE
	 * @param Wine - Файл запуска WINE
	 * @param WinePrefix - Папка с ресурсами WINDOWS, через которые будет запущена игра
	 */
	public static void saveWineConfig(String WinePath, String WinePrefix) {
		WINE_PATH = WinePath;
		WINE_PREFIX = WinePrefix;
		ConfigUtils config = new ConfigUtils(Main.getConfigDir()+File.separator+"launcher.cfg");
		config.set("winepath", WinePath);
		config.set("wineprefix", WinePrefix);
		config.save();
	}
}
