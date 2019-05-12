package ru.vadimka.nfswlauncher.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ru.vadimka.nfswlauncher.Main;
import ru.vadimka.nfswlauncher.theme.Frame;

public class ChangeUserAction implements ActionListener {
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Main.frame.changeWindow(Frame.WINDOW_LOADING);
		Thread load = new Thread(new Runnable() {
			@Override
			public void run() {
				Main.searchServers();
				Main.account.logout();
				Main.frame.changeWindow(Frame.WINDOW_LOGIN);
			}
		});
		load.start();
	}

}
