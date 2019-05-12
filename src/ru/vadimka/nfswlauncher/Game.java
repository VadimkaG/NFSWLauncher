package ru.vadimka.nfswlauncher;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;

import ru.vadimka.nfswlauncher.theme.Frame;
import ru.vadimka.nfswlauncher.utils.CheckSum;
import ru.vadimka.nfswlauncher.utils.DiscordController;

public class Game {
	
	private Process game;
	
	private static final byte[] CHECKSUM_LaunchFile = {
			(byte) 0x4c, (byte) 0x32, (byte) 0x93, (byte) 0x6d,
			(byte) 0xeb, (byte) 0xff, (byte) 0xcb, (byte) 0xdc,
			(byte) 0x20, (byte) 0x8d, (byte) 0x39, (byte) 0x50,
			(byte) 0x2b, (byte) 0x79, (byte) 0xa5, (byte) 0x6f
	};
	
	public Game(String token, String userId, String serverEnginePath, String gamePath) {
		if (!isLaunchFile(gamePath)) {
			Main.frame.showDialog(Main.locale.get("launch_error_file"), Main.locale.get("launch_error_title"));
			return;
		}
		try {
			ProcessBuilder builder;
			if (Config.WINE_PATH.equalsIgnoreCase("")) {
				builder = new ProcessBuilder(gamePath, Main.getSystemLanguage(), serverEnginePath, token, userId);
			}
			else {
				builder = new ProcessBuilder(Config.WINE_PATH, gamePath, Main.getSystemLanguage(), serverEnginePath, token, userId);
				Map<String,String> env = builder.environment();
				env.put("WINEPREFIX", Config.WINE_PREFIX);
			}
			Log.print("Запуск игры и скрытие GUI лаунчера...");
			game = builder.start();
			DiscordController.updateState("Играет на "+Main.server.getProtocol().get("SERVER_NAME"), "Игра запущена");
			inputReader.start();
			Main.frame.setVisible(false);
			if (Config.BACKGROUND_WORCK_DENY) Main.shutdown(3);
			waitForGameStoped.start();
		} catch (Exception e) {
			Log.print("Не возможно прочитать файл запуска. Закрытие лаунчера.");
			Main.frame.showDialog("Во время запуска игры произошла ошибка\nПопробуйте перекачать игру", "Не удалось запустить игру");
			Main.frame.setVisible(true);
		}
	}
	/**
	 * Ожидает пока игра завершится, чтобы продолжить работу лаунчера
	 */
	private Thread waitForGameStoped = new Thread(new Runnable() {
		public void run() {
			int exitCode;
			try {
				exitCode = game.waitFor();
				if (exitCode != 0) {
					Log.print("Игра завершилась не правильно. Код завершения: "+exitCode);
					Main.frame.changeWindow(Frame.WINDOW_LOADING);
					Main.frame.setVisible(true);
					DiscordController.updateState("","Игра не запущена");
					Main.frame.showDialog("Упс... Похоже игра завершилась с ошибкой...", "Игра завершена с ошибкой");
					try {
						Main.server.getProtocol().login(Main.account);
						Main.frame.changeWindow(Frame.WINDOW_MAIN);
					} catch (AuthException e) {
						Log.print("Ошибка авторизации.");
						Main.account.logout();
						Main.searchServers();
						Main.frame.changeWindow(Frame.WINDOW_LOGIN);
					}
				} else {
					Log.print("Игра звершена.");
					Main.shutdown(0);
				}
			} catch (InterruptedException e) {
				Log.print("Игра насильно завершена.");
				Main.shutdown(0);
			}
		}
	});
	/**
	 * Является ли файл, запускным файлом игры (nfsw.exe)
	 * @param path - Путь к файлу
	 * @return boolean
	 */
	public static boolean isLaunchFile(String path) {
		InputStream is = null;
		try {
			is = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			Log.print("Файл nfsw.exe не найден.");
			return false;
		}
		return Arrays.equals(CheckSum.get(is), CHECKSUM_LaunchFile);
	}
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
}
