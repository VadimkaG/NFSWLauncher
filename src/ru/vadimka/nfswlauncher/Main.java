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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ru.vadimka.nfswlauncher.ValueObjects.Account;
import ru.vadimka.nfswlauncher.ValueObjects.ServerVO;
import ru.vadimka.nfswlauncher.actions.ChangeLocaleAction;
import ru.vadimka.nfswlauncher.actions.ClientConfigAction;
import ru.vadimka.nfswlauncher.locales.Locale;
import ru.vadimka.nfswlauncher.protocol.RacingWorld;
import ru.vadimka.nfswlauncher.protocol.ServerInterface;
import ru.vadimka.nfswlauncher.protocol.Soapbox;
import ru.vadimka.nfswlauncher.protocol.SoapboxLocked;
import ru.vadimka.nfswlauncher.theme.Frame;
import ru.vadimka.nfswlauncher.theme.customcomponents.ComboBoxC;
import ru.vadimka.nfswlauncher.utils.AsyncTasksUtils;
import ru.vadimka.nfswlauncher.utils.DiscordController;
import ru.vadimka.nfswlauncher.utils.HTTPRequest;
import ru.vadimka.nfswlauncher.utils.RWAC;

public abstract class Main {
	public static Locale locale;
	public static ServerVO server;
	public static Account account;
	public static Frame frame;
	public static Game game;
	public static void main(String[] args) {
		Log.init();
		Log.print("Версия программы "+Config.VERSION+".");
		Log.print("Версия ОС "+System.getProperty("os.name")+", "+System.getProperty("os.arch")+".");
		Log.print("Инициализация...");
		
		Config.load();
		Log.print("Загрузка конфига завершена.");
		
		if (Config.LANGUAGE.equalsIgnoreCase("")) {
			Config.LANGUAGE = getSystemLanguage();
			Config.save();
		}
		
		RWAC.load();
		Log.print("RWAC загружен.");
		
		locale = new Locale(Config.LANGUAGE);
		locale.load();
		Log.print("Загрузка языка завершена.");
		
		ClientConfigAction.call();
		frame = new Frame();
		updateLocales();
		frame.setVisible(true);
		Log.print("Загрузка окна завершена.");
		
		account = new Account(new ServerVO(Config.SERVER_LINK, Config.SERVER_NAME), Config.USER_LOGIN, Config.USER_PASSWORD);
		account.getServer().setProtocol(genProtocolByName(Config.SERVER_PROTOCOL, account.getServer()));
		try {
			account.getServer().getProtocol().login(account);
			server = account.getServer();
			server.getProtocol().getResponse();
			Log.print("Авторизация успешна. login: "+account.getLogin());
			frame.changeWindow(Frame.WINDOW_MAIN);
		} catch (AuthException e) {
			searchServers();
			frame.changeWindow(Frame.WINDOW_LOGIN);
		}
		Log.print("Система аккаунтов и серверный протокол загружены.");
		
		DiscordController.load();
		Log.print("Discord RPC запущен...");
		
		Thread load = new Thread(new Runnable() {
			@Override
			public void run() {
				Log.print("Проверка обновлений...");
				if (checkUpdate()) {
					Log.print("Найдено новое обновление.");
					Object[] options = { locale.get("yes"), locale.get("no") };
					Integer a = frame.showQuestionDialog(locale.get("update_title"), locale.get("update").replaceFirst("%%VERSION%%", Config.UPDATE_NEW_VERSION),options );
					if (a == 0) {
						updateLauncher.start();
					} else {
						Log.print("Обновление отклонено пользователем.");
					}
				} else {
					Log.print("Обновлений не найдено");
				}
			}
		});
		load.start();
		Log.print("Инициализация завершена.");
	}
	/**
	 * Обновить список локализаций
	 */
	@SuppressWarnings("unchecked")
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
	}
	/**
	 * Получить протокол по его имени
	 * @param name - имя протокола
	 * @param vo - Сервер
	 * @return
	 */
	private static ServerInterface genProtocolByName(String name, ServerVO vo) {
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
	@SuppressWarnings("unchecked")
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
	}
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
				Log.print("Ошибка проверки обновления: Не достаточно данных в ответе. Ответ: "+result);
			}
		} else {
			Log.print("Ошибка проверки обновления: При запросе на "+Config.UPDATE_INFO_URL+" пришел пустой ответ.");
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
					
					frame.changeWindow(Frame.WINDOW_LOADING);
					Log.print("Обновление: Инициализация...");
					
					// Загрузка лаунчера
					URL url = new URL(Config.UPDATE_LINK);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					
					InputStream is = new BufferedInputStream(conn.getInputStream());
					FileOutputStream fos = new FileOutputStream(file);
					
					int bs = 0;
					byte[] buffer = new byte[65536];
					Log.print("Обновление: Загрузка данных...");
					while((bs = is.read(buffer, 0, buffer.length)) != -1) {
						fos.write(buffer, 0, bs);
					}
					is.close();
					fos.close();
					conn.disconnect();
					// ===================
					
					// Замена старого лаунчера
					Log.print("Обновление: Замена лаунчера...");
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
					
					Log.print("Обновление: Обновление завершено.");
					restart();
				} else {
					frame.showDialog(locale.get("update_error_compile"), locale.get("update_error_title"));
				}
			} catch (IOException e) {
				Log.print("Ошибка обновления: Не удалось обновить лаунчер. "+e.getLocalizedMessage());
				Log.print(e.getStackTrace());
				frame.showDialog(locale.get("update_error"), locale.get("update_error_title"));
			} catch (URISyntaxException e) {
				Log.print("Ошибка обновления: Не корректная ссылка обновления: "+Config.UPDATE_LINK);
				frame.showDialog(locale.get("update_error"), locale.get("update_error_title"));
			} catch (Exception e) {
				Log.print("Не предвиденная ошибка обновления: "+e.getLocalizedMessage());
				Log.print(e.getStackTrace());
				frame.showDialog(locale.get("update_error"), locale.get("update_error_title"));
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
			Log.print("Ошибка: Не удалось перезапустить лаунчер. "+e.getLocalizedMessage());
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
			Log.print("Ошибка: Не удалось определить рабочую деректорию. ");
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
}
