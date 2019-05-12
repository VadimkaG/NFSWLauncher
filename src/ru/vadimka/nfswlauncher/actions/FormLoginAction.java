package ru.vadimka.nfswlauncher.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import ru.vadimka.nfswlauncher.AuthException;
import ru.vadimka.nfswlauncher.Config;
import ru.vadimka.nfswlauncher.Log;
import ru.vadimka.nfswlauncher.Main;
import ru.vadimka.nfswlauncher.ValueObjects.Account;
import ru.vadimka.nfswlauncher.theme.Frame;

public class FormLoginAction implements ActionListener, Linkable {
	
	private static HashMap<String,Getter<String>> storageLinks = new HashMap<String,Getter<String>>();
	
	private static FormLoginAction INSTANCE = null;
	
	public static FormLoginAction call() {
		if (INSTANCE == null)
			INSTANCE = new FormLoginAction();
		return INSTANCE;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Main.frame.changeWindow(Frame.WINDOW_LOADING);
		Thread auth = new Thread(() -> {
			try {
				Account acc = new Account(Main.server, storageLinks.get("login").get(), storageLinks.get("password").get());
				Main.server.getProtocol().login(acc);
				Main.account = acc;
				
				Config.USER_LOGIN = acc.getLogin();
				Config.USER_PASSWORD = acc.getPassword();
				Config.save();
				Main.frame.changeWindow(Frame.WINDOW_MAIN);
			} catch (NullPointerException exn) {
				Main.frame.showDialog("Внутренняя ошибка лаунчера...", Main.locale.get("auth_error_title"));
				Log.print("Внутренняя ошибка авторизации, не удалось получить данные.");
				Log.print(exn.getStackTrace());
				Main.frame.changeWindow(Frame.WINDOW_LOGIN);
			} catch (AuthException ex) {
				Main.frame.showDialog(Main.locale.get("auth_error").replaceFirst("%%RESPONSE%%", ex.getMessage()), Main.locale.get("auth_error_title"));
				Log.print("Ошибка логина. Описание: "+ex.getMessage());
				Main.frame.changeWindow(Frame.WINDOW_LOGIN);
			}
		});
		auth.start();
	}

	@Override
	public FormLoginAction link(String key, Getter<String> g) {
		storageLinks.put(key, g);
		return this;
	}

}
