package ru.vadimka.nfswlauncher.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import ru.vadimka.nfswlauncher.AuthException;
import ru.vadimka.nfswlauncher.Log;
import ru.vadimka.nfswlauncher.Main;
import ru.vadimka.nfswlauncher.theme.Frame;

public class FormRegAction implements ActionListener, Linkable {
	
	private static HashMap<String,Getter<String>> storageLinks = new HashMap<String,Getter<String>>();
	
	private static FormRegAction INSTANCE = null;
	
	public static FormRegAction call() {
		if (INSTANCE == null)
			INSTANCE = new FormRegAction();
		return INSTANCE;
	}

	@Override
	public Linkable link(String key, Getter<String> g) {
		storageLinks.put(key, g);
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			Main.server.getProtocol().register(storageLinks.get("login").get(), storageLinks.get("password").get());
			Main.frame.showDialog(Main.locale.get("msg_reg_success"), Main.locale.get("msg_reg_success_title"));
			Main.frame.changeWindow(Frame.WINDOW_LOGIN);
		} catch (NullPointerException exn) {
			Main.frame.showDialog("Внутренняя ошибка лаунчера...", Main.locale.get("msg_reg_error_title"));
			Log.print("Внутренняя ошибка регистрации, не удалось получить данные.");
			Log.print(exn.getStackTrace());
		} catch (AuthException ex) {
			Main.frame.showDialog(Main.locale.get("msg_reg_error").replaceFirst("%%RESPONSE%%", ex.getMessage()), Main.locale.get("msg_reg_error_title"));
			Log.print("Ошибка регистрации. Ответ от сервера: "+ex.getMessage());
		}
	}

}
