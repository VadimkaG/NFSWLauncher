package ru.vadimka.nfswlauncher.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;

import ru.vadimka.nfswlauncher.AuthException;
import ru.vadimka.nfswlauncher.Config;
import ru.vadimka.nfswlauncher.Log;
import ru.vadimka.nfswlauncher.Main;
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
			//Main.frame.setVisible(false);
			if (Config.BACKGROUND_WORCK_DENY) Main.shutdown(0);
			else Main.destroyGraphic();
			new Thread(waitForGameStoped).start();
		} catch (Exception e) {
			//Main.createGraphic();
			Log.getLogger().log(Level.WARNING,"Не возможно прочитать файл запуска. Закрытие лаунчера.",e);
			if (Main.frame != null) {
				Main.frame.errorDialog("Во время запуска игры произошла ошибка\nПопробуйте перекачать игру", "Не удалось запустить игру");
				//Main.frame.setVisible(true);
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
	public boolean isAlive() {
		if (game != null && game.isAlive()) return true;
		else return false;
	}
}
