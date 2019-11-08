package ru.vadimka.nfswlauncher.theme;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ru.vadimka.nfswlauncher.Config;
import ru.vadimka.nfswlauncher.Locale;
import ru.vadimka.nfswlauncher.Log;
import ru.vadimka.nfswlauncher.Main;
import ru.vadimka.nfswlauncher.ValueObjects.Account;
import ru.vadimka.nfswlauncher.ValueObjects.ClientSettings;
import ru.vadimka.nfswlauncher.ValueObjects.ServerVO;
import ru.vadimka.nfswlauncher.client.Game;
import ru.vadimka.nfswlauncher.client.GameStartException;
import ru.vadimka.nfswlauncher.utils.AsyncTasksUtils;
import ru.vadimka.nfswlauncher.utils.HTTPRequest;

public class GraphActions {
	/**
	 * Получить список серверов
	 */
	public static List<ServerVO> getServerList() {
		try {
			
			HTTPRequest request = new HTTPRequest(Config.SERVERS_LIST_LINK);
			
			request.proc();
			
			if (request.getResponseCode() != 200) return new ArrayList<ServerVO>();
			
			String xml = request.getResponse();
			
			InputSource source = new InputSource(new StringReader(xml));
			
			final List<ServerVO> servers = new ArrayList<ServerVO>();
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = documentBuilder.parse(source);
			
			Node Servers = document.getDocumentElement();
			NodeList items = Servers.getChildNodes();
			
			for (int i = 0; i < items.getLength(); i++) {
				Node server = items.item(i);
				if (
						server.getNodeName() == "server" && 
						server.getAttributes().getNamedItem("protocol") != null && 
						server.getAttributes().getNamedItem("ip") != null) {
					
					final String protocol = new String(server.getAttributes().getNamedItem("protocol").getTextContent());
					final String ip = new String(server.getAttributes().getNamedItem("ip").getTextContent());
					final String name = new String(server.getTextContent());
					
					boolean isHttps = false;
					if (server.getAttributes().getNamedItem("https") != null  && server.getAttributes().getNamedItem("https").getTextContent().equalsIgnoreCase("true"))
						isHttps = true;
					
					final boolean https = new Boolean(isHttps);
					
					AsyncTasksUtils.addTask(() -> {
						ServerVO vo = new ServerVO(ip,name,https);
						vo.setProtocol(Main.genProtocolByName(protocol,vo));
						servers.add(vo);
					});
				}
			}
			
			try {
				AsyncTasksUtils.waitTasks();
			} catch (InterruptedException e) {}
			
			File custom_servers = new File(Main.getConfigDir()+File.separator+"servers.xml");
			
			if (custom_servers.exists() && custom_servers.canRead()) {
				documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				document = documentBuilder.parse(custom_servers.getAbsolutePath());
				
				Servers = document.getDocumentElement();
				items = Servers.getChildNodes();
				
				for (int i = 0; i < items.getLength(); i++) {
					Node server = items.item(i);
					if (server.getNodeName() == "server") {
						if (server.getAttributes().getNamedItem("ip") == null || server.getAttributes().getNamedItem("protocol").getTextContent() == null) {
							continue;
						}
						boolean isHttps = false;
						if (
								server.getAttributes().getNamedItem("https") != null
									&&
								server.getAttributes().getNamedItem("https").getTextContent().equalsIgnoreCase("true")
							) isHttps = true;
						ServerVO vo = new ServerVO(server.getAttributes().getNamedItem("ip").getTextContent(),server.getTextContent(),isHttps);
						vo.setProtocol(Main.genProtocolByName(server.getAttributes().getNamedItem("protocol").getTextContent(),vo));
						servers.add(vo);
					}
				}
			}
			return servers;
		} catch (SAXException e) {
			Log.getLogger().warning("Ошибка разбора синтаксиса, при попытке обновить список серверов.");
		} catch (ParserConfigurationException e) {
			Log.getLogger().warning("Ошибка разбора данных, при попытке обновить список серверов.");
		} catch (UnknownHostException e) {
			Log.getLogger().warning("Не удалось соединиться с сервером, чтобы получить список серверов.");
		} catch (IOException e) {
			Log.getLogger().log(Level.WARNING,"Ошибка при попытке получить список серверов.",e);
		}
		
		return new ArrayList<ServerVO>();
	}
	/**
	 * Получить текущую локализацию
	 */
	public static Locale getLocale() {
		return Main.locale;
	}
	/**
	 * Получить текущий сервер
	 */
	public static ServerVO getCurrentServer() {
		if (Main.account == null || Main.account.getServer() == null) return null;
		if (Main.account.getServer().getIP().equalsIgnoreCase("")) return null;
		return Main.account.getServer();
	}
	/**
	 * Получить текущий язык
	 */
	public static String getCurrentLocale() {
		return Config.LANGUAGE;
	}
	/**
	 * Получить имя сервера
	 */
	public static String getServerName() {
		if (Main.account == null || Main.account.getServer() == null) return "";
		return Main.account.getServer().getProtocol().get("SERVER_NAME");
	}
	/**
	 * Получить ссылку на discord текущего сервера
	 */
	public static String getServerDiscord() {
		if (Main.account == null || Main.account.getServer() == null) return "";
		return Main.account.getServer().getProtocol().get("DISCORD");
	}
	/**
	 * Получить текущий онлайн на сервере
	 */
	public static String getServerOnline() {
		if (Main.account == null || Main.account.getServer() == null) return "";
		return Main.account.getServer().getProtocol().get("PLAYERS_ONLINE");
	}
	/**
	 * Получить максимальный онлайн на сервере
	 */
	public static String getServerOnlineMax() {
		if (Main.account == null || Main.account.getServer() == null) return "";
		return Main.account.getServer().getProtocol().get("PLAYERS_MAX");
	}
	/**
	 * Получить ссылку на сайт текущего сервера
	 */
	public static String getServerWebSite() {
		if (Main.account == null || Main.account.getServer() == null) return "";
		return Main.account.getServer().getProtocol().get("WEB_SITE");
	}
	/**
	 * Получить описание текущего сервера
	 */
	public static String getServerDescription() {
		if (Main.account == null || Main.account.getServer() == null) return "";
		return Main.account.getServer().getProtocol().get("DESCRIPRION");
	}
	/**
	 * Получить имя пользователя
	 */
	public static String getUserName() {
		if (Main.account == null || Main.account.getServer() == null) return "";
		return Main.account.getLogin();
	}
	/**
	 * Получить путь к файлу игры
	 * @return
	 */
	public static String getGameFilePath() {
		return Config.GAME_PATH;
	}
	public static void setGameFilePath(String path) {
		if (Game.isLaunchFile(path)) {
			Config.GAME_PATH = path;
			//FilePathLabel.setText(Config.GAME_PATH);
			Config.save();
		} else
			Main.frame.errorDialog(Main.locale.get("launch_error_file"), Main.locale.get("launch_error_title"));
	}
	/**
	 * Изменить путь к файлу игры
	 * @param lang
	 */
	public static void changeLauncherLanguage(String lang) {
		Config.LANGUAGE = lang;
		Config.save();
		Main.destroyGraphic();
		Main.loadLocale();
		Main.createGraphic();
		//Main.restart();
	}
	/**
	 * Сменить сервер
	 * @param server - Копия объекта сервера
	 */
	public static void changeServer(ServerVO server) {
		Main.account.setServer(server);
		Main.account.getServer().getProtocol().getResponse();
	}
	/**
	 * Авторизировать пользователя
	 */
	public static void login(Account acc) {
		Main.account = acc;
		/*if (!Main.account.getServer().getIP().equalsIgnoreCase(acc.getServer().getIP()))
			Main.server = acc.getServer();*/
		Config.setServer(acc.getServer().getName(), acc.getServer().getIP(),acc.getServer().getProtocol().getNameProtocol());
		Config.setAccount(acc.getLogin(), acc.getPassword());
		Config.save();
	}
	/**
	 * Деавторизировать игрока
	 */
	public static void logout() {
		Main.account.logout();
	}
	/**
	 * Запустить игру
	 */
	public static void startGame() {
		if (Main.game != null && Main.game.isAlive()) return;
		Main.frame.loading();
		new Thread(() -> {
			if (Main.account == null) {
				Log.getLogger().warning("Ошибка: пользователь не инициализирован");
				Main.frame.loadingComplite();
				return;
			}
			if (!Main.account.isAuth()) {
				Log.getLogger().warning("Ошибка: пользователь не авторизован");
				Main.frame.loadingComplite();
				return;
			}
			if (!Main.getPlatform().equalsIgnoreCase("Windows")) {
				if (Main.getPlatform().equalsIgnoreCase("Unix")) {
					if (Config.WINE_PATH.equalsIgnoreCase("") || Config.WINE_PREFIX.equalsIgnoreCase("")) {
						boolean que = Main.frame.questionDialog(Main.locale.get("msg_wine_info_1").replace("\\n", "\n"), Main.locale.get("launch_error_title"));
						if (!que) {
							Main.frame.loadingComplite();
							return;
						}
						String WinePath = Main.frame.fileSelect();
						if (WinePath == null) {
							Main.frame.loadingComplite();
							return;
						}
						Main.frame.infoDialog(Main.locale.get("msg_wine_info_2").replace("\\n", "\n"), Main.locale.get("launch_error_title"));
						String WinePrefix = Main.frame.directorySelect();
						if (WinePrefix == null) {
							Main.frame.loadingComplite();
							return;
						}
						Config.saveWineConfig(WinePath, WinePrefix);
						Main.frame.infoDialog(Main.locale.get("msg_wine_info_2").replace("\\n", "\n"), Main.locale.get("launch_error_title"));
				} else
					Log.getLogger().info("Запуск игры в режиме wine...");
				} else if (!Main.getPlatform().equalsIgnoreCase("Unix")) {
					Main.frame.errorDialog("Похоже ваша платформа не поддерживается лаунчером.\nСожалеем, но дальше вам не пройти", Main.locale.get("launch_error_title"));
					Log.getLogger().warning("Платформа "+Main.getPlatform()+" не поддерживается. Запуск отменен.");
					Main.frame.loadingComplite();
					return;
				}
			}
			if (Config.GAME_PATH.equalsIgnoreCase("")) {
				Main.frame.errorDialog(Main.locale.get("launch_error_file_not_choosed"), Main.locale.get("launch_error_title"));
				//AChangeClient.actionPerformed(new ActionEvent(0, 0, ""));
				String filePath = Main.frame.fileSelect();
				if (filePath != null)
					GraphActions.setGameFilePath(filePath);
			}
			try {
				Main.account.getServer().getProtocol().launchGame();
			} catch (GameStartException e) {
				Log.getLogger().log(Level.WARNING,"Ошибка при попытке запуска игры",e);
				Main.frame.infoDialog(e.getMessage(), "Ошибка запуска");
				Main.frame.loadingComplite();
			}
		}).start();
	}
	/**
	 * Загрузить игровые настройки
	 */
	public static ClientSettings getGameSettings() {
		ClientSettings settings = new ClientSettings();
		List<String> SETTINGS_NAMES = (List<String>) Arrays.asList("audiomode",
				"audioquality",
				"brightness",
				"performancelevel",
				"pixelaspectratiooverride",
				"vsyncon",
				"motionblurenable",
				"overbrightenable",
				"particlesystemenable",
				"shaderdetail",
				"shadowdetail",
				"watersimenable");
		File fileConfig = Game.getConfigFile();
		if (fileConfig.exists() && fileConfig.canRead()) {
			try {
				DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document document;
				document = documentBuilder.parse(fileConfig.getAbsolutePath());
				Node Root = document.getDocumentElement();
				NodeList items = Root.getChildNodes();
				for (int i = 0; i < items.getLength(); i++) {
					Node item = items.item(i);
					switch (item.getNodeName().trim()) {
					case "UI":
						NodeList iitems = item.getChildNodes();
						for (int ii = 0; ii < iitems.getLength(); ii++) {
							Node iitem = iitems.item(ii);
							if (iitem.getNodeName().equalsIgnoreCase("Language"))
								settings.set(iitem.getNodeName().trim(),iitem.getTextContent());
						}
						break;
					case "VideoConfig":
						NodeList iitems1 = item.getChildNodes();
						for (int ii = 0; ii < iitems1.getLength(); ii++) {
							Node iitem = iitems1.item(ii);
							
							if (SETTINGS_NAMES.contains(iitem.getNodeName().trim()))
								settings.set(iitem.getNodeName().trim(),iitem.getTextContent());
						}
						break;
					default:
						break;
					}
				}
				Log.getLogger().info("Настройки клиента загружены.");
			} catch (Exception e) {
				Log.getLogger().log(Level.WARNING,"Ошибка при загрузке настроек клиента",e);
			}
		} else
			Log.getLogger().warning("Не найден файл настроек клиента");
		return settings;
	}
	/**
	 * Сохранить игровые настройки
	 */
	public static void setGameSettings(ClientSettings settings) {
		Log.getLogger().info("Сохранение настроек клиента...");
		File fileConfig = Game.getConfigFile();
		if (!fileConfig.exists() || !fileConfig.canRead() || !fileConfig.canWrite()) {
			Log.getLogger().warning("Не удалось прочесть файл настроек клиента.");
			Main.frame.infoDialog(GraphActions.getLocale().get("savesettings_error"), GraphActions.getLocale().get("savesettings_error_title"));
			return;
		}
		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document;
			document = documentBuilder.parse(fileConfig.getAbsolutePath());
			Node Root = document.getDocumentElement();
			NodeList items = Root.getChildNodes();
			for (int i = 0; i < items.getLength(); i++) {
				Node item = items.item(i);
				switch (item.getNodeName().trim()) {
				case "UI":
					NodeList iitems = item.getChildNodes();
					for (int ii = 0; ii < iitems.getLength(); ii++) {
						Node iitem = iitems.item(ii);
						if (iitem.getNodeName().equalsIgnoreCase("Language"))
							iitem.setTextContent(settings.get(iitem.getNodeName().trim()));
					}
					break;
				case "VideoConfig":
					NodeList iitems1 = item.getChildNodes();
					for (int ii = 0; ii < iitems1.getLength(); ii++) {
						Node iitem = iitems1.item(ii);
						if (settings.contents(iitem.getNodeName().trim()))
							iitem.setTextContent(settings.get(iitem.getNodeName().trim()));
					}
					break;
				default:
					break;
				}
			}
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			Result output = new StreamResult(fileConfig);
			Source input = new DOMSource(document);
			transformer.transform(input, output);
		} catch (Exception e) {
			Log.getLogger().log(Level.WARNING,"Ошабка при разборе настроек клиента",e);
		}
	}
	/**
	 * Установить сервер
	 * @param server объект сервера
	 */
	public static void setServer(ServerVO server) {
		Main.account.setServer(server);
	}
	/**
	 * Закрывать ли лаунчер после запуска игры
	 */
	public static boolean getBackgroundWork() {
		return Config.BACKGROUND_WORCK_DENY;
	}
	/**
	 * Проверять ли обновления при запуске лаунчера
	 */
	public static boolean getIsUpdateCheck() {
		return Config.IS_UPDATE_CHECK;
	}
	/**
	 * Установить "Закрывать ли лаунчер после запуска игры"
	 * @param b - Логическое значение
	 */
	public static void setLauncherSettings(boolean BackgroundWork, boolean IsUpdateCheck, boolean discordAllow, boolean is_dynamic_background) {
		Config.BACKGROUND_WORCK_DENY = BackgroundWork;
		Config.IS_UPDATE_CHECK = IsUpdateCheck;
		Config.DISCORD_ALLOW = discordAllow;
		Config.IS_DYNAMIC_BACKGROUND = is_dynamic_background;
		Config.save();
	}
	/**
	 * Авторизован ли пользователь
	 */
	public static boolean isAuthed() {
		if (Main.account == null) return false;
		return Main.account.isAuth();
	}
	/**
	 * Разрешить ли интеграцию дискорда
	 */
	public static boolean getDiscordAllow() {
		return Config.DISCORD_ALLOW;
	}
	/**
	 * Разрешить ли динамиечкий фон
	 */
	public static boolean isDynamicBackground() {
		return Config.IS_DYNAMIC_BACKGROUND;
	}
}
