package ru.vadimka.nfswlauncher;

import java.awt.Font;
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
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import ru.vadimka.nfswlauncher.ValueObjects.Account;
import ru.vadimka.nfswlauncher.ValueObjects.ServerVO;
import ru.vadimka.nfswlauncher.client.Game;
import ru.vadimka.nfswlauncher.protocol.RacingWorld;
import ru.vadimka.nfswlauncher.protocol.ServerInterface;
import ru.vadimka.nfswlauncher.protocol.Soapbox;
import ru.vadimka.nfswlauncher.protocol.SoapboxLocked;
import ru.vadimka.nfswlauncher.theme.GUI;
import ru.vadimka.nfswlauncher.theme.GUIResourseLoader;
import ru.vadimka.nfswlauncher.theme.GraphActions;
import ru.vadimka.nfswlauncher.theme.GraphModule;
import ru.vadimka.nfswlauncher.theme.InfoDialog;
import ru.vadimka.nfswlauncher.theme.LogWindow;
import ru.vadimka.nfswlauncher.theme.NullGUI;
import ru.vadimka.nfswlauncher.utils.DiscordController;
import ru.vadimka.nfswlauncher.utils.HTTPRequest;

public abstract class Main {
	private static Logger logger;
	public static Locale locale;
	public static Account account;
	public static GraphModule frame = null;
	public static Game game;
	public static void main(String[] args) {
		System.setProperty("file.encoding", "UTF-8");
		boolean autoauth = true;
		boolean showChangelog = false;
		boolean fastJoin = false;
		for (String str : args) {
			switch (str) {
			case "noautoauth":
				autoauth = false;
				break;
			case "showChangelog":
				showChangelog = true;
				break;
			}
			if (str.length() >= 8 && str.substring(0,8).equalsIgnoreCase("fastjoin")) {
				fastJoin = true;
			}
		}
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (game != null && !game.succesStarted()) game.killGame();
				DiscordController.shutdown();
			}
		});
		if (fastJoin == false || !rawLauncher())
			init(autoauth,showChangelog);
	}
	public static boolean rawLauncher() {
		logger = LogManager.getLogManager().getLogger("");
		Log.setLogger(logger);
		Config.load();
		DiscordController.load();
		frame = new NullGUI();
		account = new Account(new ServerVO(Config.SERVER_LINK, Config.SERVER_NAME,false), Config.USER_LOGIN, Config.USER_PASSWORD);
		account.getServer().setProtocol(genProtocolByName(Config.SERVER_PROTOCOL, account.getServer()));
		if (!account.getLogin().equalsIgnoreCase("") && !account.getServer().getIP().equalsIgnoreCase("")) {
			try {
				account.getServer().getProtocol().login(account);
				account.getServer().getProtocol().getResponse();
				if (Config.GAME_PATH.equalsIgnoreCase("")) return false;
				Main.account.getServer().getProtocol().launchGame();
				return true;
			} catch (Exception e) {
				Log.getLogger().log(Level.WARNING,"Ошибка запуска",e);
			}
		}
		return false;
	}
	public static void init(boolean autoauth,boolean showChangelog) {
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
			Log.getLogger().info("Версия java "+System.getProperty("java.version")+" "+System.getProperty("sun.arch.data.model"));
			Log.getLogger().info("Инициализация...");
			
			System.setProperty("Djava.awt.headless", "true");
			
			Config.load();
			Log.getLogger().info("Загрузка конфига завершена.");
			
			if (Config.LANGUAGE.equalsIgnoreCase("")) {
				Config.LANGUAGE = getSystemLanguage();
				Config.save();
			}
			
			loadLocale();
			Log.getLogger().info("Загрузка языка завершена.");
			
			//frame = new Frame();
			Font f = GUIResourseLoader.loadFont();
			if (f != null) {
				FontUIResource fur = new FontUIResource(GUIResourseLoader.loadFont());
				Enumeration<Object> keys = UIManager.getDefaults().keys();
				while (keys.hasMoreElements()) {
					Object key = keys.nextElement();
					Object value = UIManager.get (key);
					if (value instanceof javax.swing.plaf.FontUIResource)
						UIManager.put (key, fur);
				}
			}
			createGraphic();
			Log.getLogger().info("Загрузка окна завершена.");
			
			Log.getLogger().info("Инициализация сохраненного аккаунта...");
			account = new Account(new ServerVO(Config.SERVER_LINK, Config.SERVER_NAME,false), Config.USER_LOGIN, Config.USER_PASSWORD);
			account.getServer().setProtocol(genProtocolByName(Config.SERVER_PROTOCOL, account.getServer()));
			try {
				if (!account.getServer().getIP().isEmpty()) {
					if (autoauth && !account.getLogin().isEmpty()) {
						account.getServer().getProtocol().login(account);
						account.getServer().getProtocol().getResponse();
						Log.getLogger().info("Авторизация успешна. login: "+account.getLogin());
						frame.setLogin(true);
					} else {
						if (account.getServer().getProtocol() != null)
							account.getServer().getProtocol().getResponse();
						frame.updateServers(GraphActions.getServerList());
						frame.setLogin(false);
					}
				} else {
					frame.updateServers(GraphActions.getServerList());
					frame.setLogin(false);
				}
			} catch (AuthException e) {
				frame.updateServers(GraphActions.getServerList());
				frame.setLogin(false);
			}
			Log.getLogger().info("Система аккаунтов и серверный протокол загружены.");
			
			DiscordController.load();
			if (Config.DISCORD_ALLOW)
				Log.getLogger().info("Интеграция discord запущена.");
			
			if (frame != null)
				frame.loadingComplite();
			if (Config.IS_UPDATE_CHECK == true) {
				Thread th = new Thread(checkUpdate);
				th.start();
			} else Log.getLogger().info("Проверка обновлений отключена. Игнорирую обновления...");
			Log.getLogger().info("Инициализация завершена.");
			if (showChangelog)
				new InfoDialog().show();
		} catch (Throwable ex) {
			logger.log(Level.SEVERE, "Критическая ошибка", ex);
			String msg = "Обнаружена критическая ошибка, которая не позволяет программе работать.\n"
					+ "Пожалуйста сообщите о ней разработчику лаунчера\n"
					+ "Email: vadik.golubeff@yandex.ru\n"
					+ "Ниже приведен отчет о работе лаунчера.";
			if (Main.locale != null)
				msg = Main.locale.get("critical error").replace("\\n", "\n");
			new LogWindow().setDescription(msg).setExitOnClose();
		}
	}
	private static Runnable checkUpdate = new Runnable() {
		@Override
		public void run() {
			Log.getLogger().info("Проверка обновлений...");
			if (checkUpdate()) {
				Log.getLogger().info("Найдено новое обновление.");
				/*Object[] options = { locale.get("yes"), locale.get("no") };
				Integer a = frame.showQuestionDialog(locale.get("update_title"), locale.get("update").replaceFirst("%%VERSION%%", Config.UPDATE_NEW_VERSION),options );*/
				if (frame.questionDialog(locale.get("update").replaceFirst("%%VERSION%%", Config.UPDATE_NEW_VERSION), locale.get("update_title")))
					new Thread(updateLauncher).start();
				else
					Log.getLogger().info("Обновление отклонено пользователем.");
			} else {
				Log.getLogger().info("Обновлений не найдено");
			}
		}
	};
	/**
	 * Получить протокол по его имени
	 * @param name - имя протокола
	 * @param vo - Сервер
	 * @return
	 */
	public static ServerInterface genProtocolByName(String name, ServerVO vo) {
		switch(name.trim()) {
		case "soapbox-Locked":
			return new SoapboxLocked(vo);
		case "RacingWorld":
			return new RacingWorld(vo);
		default:
			return new Soapbox(vo);
		}
	}
	/**
	 * Получить список протоколов
	 * @return
	 */
	public static List<String> getProtocols() {
		List<String> protocols = new ArrayList<String>();
		protocols.add("soapbox");
		protocols.add("soapbox-Locked");
		protocols.add("RacingWorld");
		return protocols;
	}
	/**
	 * Проверить вышло ли обновление лаунчера
	 * @return
	 */
	public static boolean checkUpdate() {
		HTTPRequest.ActionAutoContainer response = new HTTPRequest.ActionAutoContainer();
		HTTPRequest request = new HTTPRequest(Config.UPDATE_INFO_URL,response);
		request.proc();
		request.waitResponse();
		String result = response.toString();
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
	public static Runnable updateLauncher = new Runnable() {
		@Override
		public void run() {
			try {
				File file = new File(getConfigDir()+File.separator+"launcher_update.tmp");
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
					String[] args = {
						"showChangelog"
					};
					restart(args);
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
	};
	public static void restart() {
		restart(null);
	}
	/**
	 * Перезапустить лаунчер
	 */
	public static void restart(String[] args) {
		try {
			ArrayList<String> params = new ArrayList<String>();
			params.add(System.getProperty("java.home")+File.separator+"bin"+File.separator+"java");
			params.add("-classpath");
			params.add(ru.vadimka.nfswlauncher.Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			params.add(ru.vadimka.nfswlauncher.Main.class.getCanonicalName());
			if (args != null)
				for (String param : args) {
					params.add(param);
				}
			ProcessBuilder pb = new ProcessBuilder(params);
			Process process = pb.start();
			if (process == null) throw new Exception("Ошибка запуска лаунера!");
		} catch (Exception e) {
			Log.getLogger().warning("Ошибка: Не удалось перезапустить лаунчер. "+e.getLocalizedMessage());
		}
		System.exit(0);
	}
	/**
	 * Получить рабочую директорию лаунчера
	 * @return
	 */
	public static File getConfigDir() {
		File wrkDir = null;
		File userHome = new File(System.getProperty("user.home","."),Config.WINDOW_TITLE);
		File local_share = new File(System.getProperty("user.home","."),".local"+File.separator+"share"+File.separator+Config.WINDOW_TITLE);
		File appdata = new File(System.getenv("APPDATA"),"Need for Speed World");
		if (appdata.getParentFile() != null && appdata.getParentFile().exists())
			wrkDir = appdata;
		else if (local_share.getParentFile() != null && local_share.getParentFile().exists())
			wrkDir = local_share;
		else if (userHome.getParentFile() != null && userHome.getParentFile().exists())
			wrkDir = userHome;
		else {
			Log.getLogger().warning("Ошибка: Не удалось определить рабочую деректорию. ");
			throw new RuntimeException("Рабочая директория не определена.");
		}
		if (!wrkDir.exists() && !wrkDir.mkdirs()) {
			Log.getLogger().warning("Ошибка: Не удалось определить рабочую деректорию. ");
			throw new RuntimeException("Рабочая директория не определена.");
		}
		return wrkDir;
	}
	/**
	 * Получить рабочую директорию лаунчера
	 * @return
	 */
	public static File getGameDir() {
		return new File(Config.GAME_PATH);
	}
	/**
	 * Получить язык установленный в операционной система
	 * @return id локализации
	 */
	public static String getSystemLanguage() {
		return System.getProperty("user.language");
	}
	/**
	 * Кэш платформы. Чтобы не проверять ее каждый раз
	 */
	private static String CHACHE_PLATFORM;
	/**
	 * Получить операционную систему
	 * @return
	 */
	public static String getPlatform() {
		if (CHACHE_PLATFORM != null) return CHACHE_PLATFORM;
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("linux") || osName.contains("unix")) {
			CHACHE_PLATFORM = "Unix";
			return "Unix";
		}
		else if (osName.contains("win")) {
			CHACHE_PLATFORM = "Windows";
			return "Windows";
		}
		else if (osName.contains("mac")) {
			CHACHE_PLATFORM = "Mac";
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
		//frame= new NullGUI();
		frame.updateLocales(Locale.values());
		frame.setVisible(true);
		if (Main.account != null) {
			frame.setLogin(Main.account.isAuth());
			frame.loadingComplite();
		}
	}
}
