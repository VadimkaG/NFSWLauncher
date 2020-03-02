package ru.vadimka.nfswlauncher.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import ru.vadimka.nfswlauncher.AuthException;
import ru.vadimka.nfswlauncher.Config;
import ru.vadimka.nfswlauncher.Log;
import ru.vadimka.nfswlauncher.Main;
import ru.vadimka.nfswlauncher.ProcessUtils;
import ru.vadimka.nfswlauncher.theme.GraphActions;
import ru.vadimka.nfswlauncher.utils.DiscordController;

public class Game {
	
	private Process game;
	
	public Game(String token, String userId, String serverEnginePath, String gamePath) {
		try {
			if (Main.frame != null)
				Main.frame.loading();
			ProcessBuilder builder;
			if (Config.WINE_PATH.equalsIgnoreCase("")) {
				builder = new ProcessBuilder(gamePath, Main.getSystemLanguage().toUpperCase(), serverEnginePath, token, userId);
			} else {
				builder = new ProcessBuilder(Config.WINE_PATH, gamePath, Main.getSystemLanguage(), serverEnginePath, token, userId);
				Map<String,String> env = builder.environment();
				env.put("WINEPREFIX", Config.WINE_PREFIX);
			}
			Log.getLogger().info("Запуск игры и скрытие GUI лаунчера...");
			game = builder.start();
			DiscordController.updateState("Играет на "+Main.account.getServer().getProtocol().get("SERVER_NAME"), "Игра запущена");
			inputReader.start();
			
			Timer timer = new Timer();
			timer.schedule(renamerTask, 6000);
			
			if (Config.BACKGROUND_WORCK_DENY) Main.shutdown(0);
			else Main.destroyGraphic();
			new Thread(waitForGameStoped).start();
		} catch (Exception e) {
			Main.createGraphic();
			Log.getLogger().log(Level.WARNING,"Не удалось запустить игру. Закрытие лаунчера.",e);
			if (Main.frame != null) {
				Main.frame.errorDialog("Во время запуска игры произошла ошибка\nПопробуйте перекачать игру", "Не удалось запустить игру");
				Main.frame.loadingComplite();
			}
		}
	}
	/**
	 * Ожидает пока игра завершится, чтобы продолжить работу лаунчера
	 */
	private Runnable waitForGameStoped = new Runnable() {
		public void run() {
			int exitCode;
			try {
				exitCode = game.waitFor();
				if (exitCode != 0) {
					Main.createGraphic();
					Log.getLogger().warning("Игра завершилась не правильно. Код завершения: "+exitCode);
					//Main.frame.changeWindow(Frame.WINDOW_LOADING);
					Main.frame.loading();
					Main.frame.setVisible(true);
					DiscordController.updateState("","Игра не запущена");
					Main.frame.errorDialog("Упс... Похоже игра завершилась с ошибкой...", "Игра завершена с ошибкой");
					try {
						Main.account.getServer().getProtocol().login(Main.account);
						//Main.frame.changeWindow(Frame.WINDOW_MAIN);
						Main.frame.setLogin(true);
					} catch (AuthException e) {
						Log.getLogger().warning("Ошибка авторизации.");
						Main.account.logout();
						//Main.searchServers();
						Main.frame.updateServers(GraphActions.getServerList());
						//Main.frame.changeWindow(Frame.WINDOW_LOGIN);
						Main.frame.setLogin(false);
					}
					Main.frame.loadingComplite();
				} else {
					Log.getLogger().info("Игра звершена.");
					Main.shutdown(0);
				}
			} catch (InterruptedException e) {
				Log.getLogger().info("Игра насильно завершена.");
				Main.shutdown(0);
			}
		}
	};
	/**
	 * Читает inputstream игры
	 */
	private Thread inputReader = new Thread(() -> {
		InputStream is = game.getInputStream();
		byte[] b = new byte[1024];
		while (game.isAlive()) {
			try {
				is.read(b);
			} catch (IOException e) {}
		}
	});
	/**
	 * Поулчить файл конфига игры
	 * @return
	 */
	public static File getConfigFile() {
		File gameConfig = null;
		if (!Main.getPlatform().equalsIgnoreCase("Windows") && !Config.WINE_PREFIX.equalsIgnoreCase("")) {
			File users = new File(Config.WINE_PREFIX+File.separator+"drive_c"+File.separator+"users");
			if (users.exists()) {
				String username = null;
				File[] files = users.listFiles();
				if (files != null)
					for (File file : files) {
						if (file.getName().equalsIgnoreCase("Public")) continue;
						username = file.getName();
						break;
					}
				if (username != null) {
					gameConfig = new File(
							users.getAbsolutePath()+File.separator+
							username+File.separator+
							"Application Data"+File.separator+
							"Need for Speed World"+File.separator+
							"Settings"+File.separator+
							"UserSettings.xml"
						);
					if (gameConfig.exists()) return gameConfig;
				}
			}
		} else
			gameConfig = new File(Main.getConfigDir()+File.separator+"Settings"+File.separator+"UserSettings.xml");
		return gameConfig;
	}
	/**
	 * Запущена ли игра
	 * @return
	 */
	public boolean isAlive() {
		if (game != null && game.isAlive()) return true;
		else return false;
	}
	/**
	 * Задача по переименовыванию окна игры
	 * Поддерживается только Windows
	 * TODO: Добавить поддержку других операционных систем
	 */
	private TimerTask renamerTask = new TimerTask() {
		@Override
		public void run() {
			int leftLimit = 97;
			int rightLimit = 122;
			int targetStringLength = 10;
			Random random = new Random();
			StringBuilder buffer = new StringBuilder(targetStringLength);
			for (int i = 0; i < targetStringLength; i++) {
				int randomLimitedInt = leftLimit + (int) 
				  (random.nextFloat() * (rightLimit - leftLimit + 1));
				buffer.append((char) randomLimitedInt);
			}
			String windowTitle = "Racing World (AntiCheat-" + buffer.toString() + ")";
			if (Main.getPlatform() == "Windows") {
				try {
					final Field f = game.getClass().getDeclaredField("handle");
					f.setAccessible(true);
					final long handle = f.getLong(game);
					f.setAccessible(false);
					ProcessUtils.renameTitle(windowTitle, handle);
				}
				catch (UnsatisfiedLinkError | IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
					Log.getLogger().log(Level.WARNING, "\u041e\u0448\u0438\u0431\u043a\u0430: \u041e\u0431\u043d\u0430\u0440\u0443\u0436\u0435\u043d\u0430 \u043f\u043e\u043f\u044b\u0442\u043a\u0430 \u043e\u0431\u0445\u043e\u0434\u0430 RWAC. \u0418\u0433\u0440\u0430 \u0443\u043d\u0438\u0447\u0442\u043e\u0436\u0435\u043d\u0430.", e);
					game.destroy();
				}
			}
		}
	};
}
