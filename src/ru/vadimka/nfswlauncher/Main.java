package ru.vadimka.nfswlauncher;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import ru.vadimka.nfswlauncher.ValueObjects.Account;
import ru.vadimka.nfswlauncher.ValueObjects.ServerVO;
import ru.vadimka.nfswlauncher.client.Game;
import ru.vadimka.nfswlauncher.protocol.RacingWorld;
import ru.vadimka.nfswlauncher.protocol.ServerInterface;
import ru.vadimka.nfswlauncher.protocol.Soapbox;
import ru.vadimka.nfswlauncher.protocol.SoapboxLocked;
import ru.vadimka.nfswlauncher.theme.GUI;
import ru.vadimka.nfswlauncher.theme.GraphActions;
import ru.vadimka.nfswlauncher.theme.GraphModule;
import ru.vadimka.nfswlauncher.theme.LogWindow;
import ru.vadimka.nfswlauncher.utils.DiscordController;
import ru.vadimka.nfswlauncher.utils.HTTPRequest;

public abstract class Main {
	private static Logger logger;
	public static Locale locale;
	public static ServerVO server;
	public static Account account;
	public static GraphModule frame = null;
	public static Game game;
	public static void main(String[] args) {init();}
	public static void init() {
		Main.getWorkDir();
		LogManager.getLogManager().reset();
		logger = LogManager.getLogManager().getLogger("");
		if (Config.MODE_LOG_CONSOLE)
			logger.addHandler(new Log.LogHandler());
		if (Config.MODE_LOG_FILE) {
			logger.addHandler(new Log.FLogHandler(Log.getLogFilePath()));
		}
		
		try {
			Log.setLogger(logger);
			
			Log.getLogger().info("Версия программы "+Config.VERSION+".");
			Log.getLogger().info("Версия ОС "+System.getProperty("os.name")+", "+System.getProperty("os.arch")+".");
			Log.getLogger().info("Версия java "+System.getProperty("java.version"));
			Log.getLogger().info("Инициализация...");
			
			Config.load();
			Log.getLogger().info("Загрузка конфига завершена.");
			
			if (Config.LANGUAGE.equalsIgnoreCase("")) {
				Config.LANGUAGE = getSystemLanguage();
				Config.save();
			}
			
			/*RWAC.load();
			Log.getLogger().info("RWAC загружен.");*/
			
			loadLocale();
			Log.getLogger().info("Загрузка языка завершена.");
			
			//ClientConfigAction.call();
			//frame = new Frame();
			createGraphic();
			Log.getLogger().info("Загрузка окна завершена.");
			
			try {
				account = new Account(new ServerVO(Config.SERVER_LINK, Config.SERVER_NAME), Config.USER_LOGIN, Config.USER_PASSWORD);
				account.getServer().setProtocol(genProtocolByName(Config.SERVER_PROTOCOL, account.getServer()));
				account.getServer().getProtocol().login(account);
				server = account.getServer();
				server.getProtocol().getResponse();
				Log.getLogger().info("Авторизация успешна. login: "+account.getLogin());
				//frame.changeWindow(Frame.WINDOW_MAIN);
				frame.setLogin(true);
			} catch (AuthException e) {
				//searchServers();
				frame.updateServers(GraphActions.getServerList());
				//frame.changeWindow(Frame.WINDOW_LOGIN);
				frame.setLogin(false);
			} catch (Exception e) {
				account = null;
				server = null;
			}
			Log.getLogger().info("Система аккаунтов и серверный протокол загружены.");
			
			DiscordController.load();
			Log.getLogger().info("Discord RPC запущен...");
			
			Thread load = new Thread(new Runnable() {
				@Override
				public void run() {
					Log.getLogger().info("Проверка обновлений...");
					if (checkUpdate()) {
						Log.getLogger().info("Найдено новое обновление.");
						/*Object[] options = { locale.get("yes"), locale.get("no") };
						Integer a = frame.showQuestionDialog(locale.get("update_title"), locale.get("update").replaceFirst("%%VERSION%%", Config.UPDATE_NEW_VERSION),options );*/
						if (frame.questionDialog(locale.get("update").replaceFirst("%%VERSION%%", Config.UPDATE_NEW_VERSION), locale.get("update_title"))) {
							updateLauncher.start();
						} else {
							Log.getLogger().info("Обновление отклонено пользователем.");
						}
					} else {
						Log.getLogger().info("Обновлений не найдено");
					}
				}
			});
			frame.loadingComplite();
			load.start();
			Log.getLogger().info("Инициализация завершена.");
		} catch (Throwable ex) {
			logger.log(Level.SEVERE, "Критическая ошибка", ex);
			new LogWindow().setDescription("Обнаружена критическая ошибка, которая не позволяет программе работать.\n"
					+ "Пожалуйста сообщите о ней разработчику лаунчера\n"
					+ "Email: vadik.golubeff@yandex.ru\n"
					+ "Ниже приведен отчет о работе лаунчера.");
		}
	}
	/**
	 * Обновить список локализаций
	 */
	/*@SuppressWarnings("unchecked")
	public static void updateLocales() {
		
		ComboBoxC<Locale> locs = null;
		
		if (frame.getWindow(Frame.WINDOW_SETTINGS).getComponent("LanguageList") instanceof ComboBoxC<?>)
			locs = (ComboBoxC<Locale>) frame.getWindow(Frame.WINDOW_SETTINGS).getComponent("LanguageList");
		
		if (locs != null) {
			locs.removeAllItems();
			locs.addItem(locale);
			
			if (!locale.getID().equalsIgnoreCase("ru"))
				locs.addItem(new Locale("ru", "Русский"));
			
			if (!locale.getID().equalsIgnoreCase("en"))
				locs.addItem(new Locale("en", "English"));
		}
		ChangeLocaleAction.on();
	}*/
	/**
	 * Получить протокол по его имени
	 * @param name - имя протокола
	 * @param vo - Сервер
	 * @return
	 */
	public static ServerInterface genProtocolByName(String name, ServerVO vo) {
		switch(name.trim()) {
		case "soapbox-Locked":
			return new SoapboxLocked(vo).getResponse();
		case "RacingWorld":
			return new RacingWorld(vo).getResponse();
		default:
			return new Soapbox(vo);
		}
	}
	/**
	 * Обновить список серверов
	 */
	/*@SuppressWarnings("unchecked")
	public static void searchServers() {
		try {
			
			ComboBoxC<ServerVO> ServesComboBox = null;
			
			if (frame.getWindow(Frame.WINDOW_LOGIN).getComponent("ServersList") instanceof ComboBoxC<?>) {
				ServesComboBox = (ComboBoxC<ServerVO>) frame.getWindow(Frame.WINDOW_LOGIN).getComponent("ServersList");
				ServesComboBox.removeAllItems();
			}
			
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = documentBuilder.parse(Config.SERVERS_LIST_LINK);
			
			Node Servers = document.getDocumentElement();
			NodeList items = Servers.getChildNodes();
			
			for (int i = 0; i < items.getLength(); i++) {
				Node server = items.item(i);
				if (server.getNodeName() == "server") {
					AsyncTasksUtils.addTask(AsyncTasksUtils.call().new Task(() -> {
						ComboBoxC<ServerVO> ServesComboBoxI = null;
						
						if (frame.getWindow(Frame.WINDOW_LOGIN).getComponent("ServersList") instanceof ComboBoxC<?>) {
							ServesComboBoxI = (ComboBoxC<ServerVO>) frame.getWindow(Frame.WINDOW_LOGIN).getComponent("ServersList");
						}
						ServerVO vo = new ServerVO(server.getAttributes().getNamedItem("ip").getTextContent(),server.getTextContent());
						vo.setProtocol(genProtocolByName(server.getAttributes().getNamedItem("protocol").getTextContent(),vo));
						if (ServesComboBoxI != null)
							ServesComboBoxI.addItem(vo);
					}));
				}
			}
			
			try {
				synchronized (AsyncTasksUtils.call()) {
					AsyncTasksUtils.call().wait();
				}
			} catch (InterruptedException e) {}
			
			File custom_servers = new File(getWorkDir()+File.separator+"servers.xml");
			
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
						ServerVO vo = new ServerVO(server.getAttributes().getNamedItem("ip").getTextContent(),server.getTextContent());
						vo.setProtocol(genProtocolByName(server.getAttributes().getNamedItem("protocol").getTextContent(),vo));
						if (ServesComboBox != null)
							ServesComboBox.addItem(vo);
					}
				}
			}
			
			if (ServesComboBox != null) {
				ServesComboBox.updateUI();
			}
			
		} catch (ParserConfigurationException e) {
			Log.print("Ошибка разбора данных, при попытке обновить список серверов.");
		} catch (SAXException e) {
			Log.print("Ошибка разбора синтаксиса, при попытке обновить список серверов.");
		} catch (IOException e) {
			Log.print(e.getStackTrace());
		}
	}*/
	/**
	 * Проверить вышло ли обновление лаунчера
	 * @return
	 */
	public static boolean checkUpdate() {
		HTTPRequest request = new HTTPRequest(Config.UPDATE_INFO_URL);
		request.proc();
		String result = request.getResponse();
		if (result != null) {
			String[] info = result.split(";");
			if (info.length >= 2) {
				Config.UPDATE_NEW_VERSION = info[0];
				Config.UPDATE_LINK = info[1];
				if (!Config.UPDATE_NEW_VERSION.equalsIgnoreCase(Config.VERSION)) return true;
			} else {
				Log.getLogger().warning("Ошибка проверки обновления: Не достаточно данных в ответе. Ответ: "+result);
			}
		} else {
			Log.getLogger().warning("Ошибка проверки обновления: При запросе на "+Config.UPDATE_INFO_URL+" пришел пустой ответ.");
		}
		return false;
	}
	/**
	 * Обновление лаунчера
	 */
	public static Thread updateLauncher = new Thread(new Runnable() {
		@Override
		public void run() {
			try {
				File file = new File(getWorkDir()+File.separator+"launcher_update.tmp");
				File fileLauncher = new File(ru.vadimka.nfswlauncher.Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
				if (!fileLauncher.isDirectory()) {
					
					//frame.changeWindow(Frame.WINDOW_LOADING);
					frame.loading();
					Log.getLogger().info("Обновление: Инициализация...");
					
					// Загрузка лаунчера
					URL url = new URL(Config.UPDATE_LINK);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					
					InputStream is = new BufferedInputStream(conn.getInputStream());
					FileOutputStream fos = new FileOutputStream(file);
					
					int bs = 0;
					byte[] buffer = new byte[65536];
					Log.getLogger().info("Обновление: Загрузка данных...");
					while((bs = is.read(buffer, 0, buffer.length)) != -1) {
						fos.write(buffer, 0, bs);
					}
					is.close();
					fos.close();
					conn.disconnect();
					// ===================
					
					// Замена старого лаунчера
					Log.getLogger().info("Обновление: Замена лаунчера...");
					FileInputStream fis = new FileInputStream(file);
					fos = new FileOutputStream(fileLauncher);

					bs = 0;
					buffer = new byte[65536];
					while((bs = fis.read(buffer, 0, buffer.length)) != -1) {
						fos.write(buffer, 0, bs);
					}
					fis.close();
					fos.close();
					Files.delete(file.toPath());
					// =======================
					
					Log.getLogger().info("Обновление: Обновление завершено.");
					restart();
				} else {
					//frame.showDialog(locale.get("update_error_compile"), locale.get("update_error_title"));
					frame.infoDialog(locale.get("update_error_compile"), locale.get("update_error_title"));
				}
			} catch (IOException e) {
				Log.getLogger().log(Level.WARNING,"Ошибка обновления: Не удалось обновить лаунчер. "+e.getLocalizedMessage(),e);
				frame.errorDialog(locale.get("update_error"), locale.get("update_error_title"));
			} catch (URISyntaxException e) {
				Log.getLogger().warning("Ошибка обновления: Не корректная ссылка обновления: "+Config.UPDATE_LINK);
				frame.errorDialog(locale.get("update_error"), locale.get("update_error_title"));
			} catch (Exception e) {
				Log.getLogger().log(Level.WARNING,"Не предвиденная ошибка обновления: "+e.getLocalizedMessage(), e);
				//frame.showDialog(locale.get("update_error"), locale.get("update_error_title"));
				frame.errorDialog(locale.get("update_error"), locale.get("update_error_title"));
			}
		}
	});
	/**
	 * Перезапустить лаунчер
	 */
	public static void restart() {
		try {
			ArrayList<String> params = new ArrayList<String>();
			params.add(System.getProperty("java.home")+File.separator+"bin"+File.separator+"java");
			params.add("-classpath");
			params.add(ru.vadimka.nfswlauncher.Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			params.add(ru.vadimka.nfswlauncher.Main.class.getCanonicalName());
			ProcessBuilder pb = new ProcessBuilder(params);
			Process process = pb.start();
			if (process == null) throw new Exception("Ошибка запуска лаунера!");
		} catch (Exception e) {
			Log.getLogger().warning("Ошибка: Не удалось перезапустить лаунчер. "+e.getLocalizedMessage());
		}
		shutdown(0);
	}
	/**
	 * Получить рабочую директорию лаунчера
	 * @return
	 */
	public static File getWorkDir() {
		File wrkDir = null;
		String userHome = System.getProperty("user.home",".");
		String appdata = System.getenv("APPDATA");
		if (appdata != null)
			wrkDir = new File(appdata,"Need for Speed World"+File.separator);
		else
			wrkDir = new File(userHome,"Need for Speed World"+File.separator);
		if ((!wrkDir.exists()) && (!wrkDir.mkdirs())) {
			Log.getLogger().warning("Ошибка: Не удалось определить рабочую деректорию. ");
			throw new RuntimeException("Рабочая директория не определена.");
		}
		return wrkDir;
	}
	/**
	 * Получить язык установленный в операционной система
	 * @return id локализации
	 */
	public static String getSystemLanguage() {
		return System.getProperty("user.language");
	}
	/**
	 * Завершение работы лаунчера
	 * @param i - код лаунчера
	 */
	public static void shutdown(int i) {
		DiscordController.shutdown();
		System.exit(i);
	}
	/**
	 * Получить операционную систему
	 * @return
	 */
	public static String getPlatform() {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("linux") || osName.contains("unix")) {
			return "Unix";
		}
		else if (osName.contains("win")) {
			return "Windows";
		}
		else if (osName.contains("mac")) {
			return "Mac";
		}
		else return osName;
	}
	/**
	 * Загрузить локаль
	 */
	public static void loadLocale() {
		locale = Locale.getLocaleById(Config.LANGUAGE);
		locale.load();
	}
	/**
	 * Уничтожить всю графику
	 */
	public static void destroyGraphic() {
		frame.destroy();
		frame = null;
		System.gc();
	}
	/**
	 * Создать графику
	 */
	public static void createGraphic() {
		if (frame != null) return;
		frame = new GUI();
		frame.updateLocales(Locale.values());
		frame.setVisible(true);
		if (Main.account != null) {
			frame.setLogin(Main.account.isAuth());
			frame.loadingComplite();
		}
	}
}
