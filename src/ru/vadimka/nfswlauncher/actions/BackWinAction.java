package ru.vadimka.nfswlauncher.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ru.vadimka.nfswlauncher.Main;
import ru.vadimka.nfswlauncher.theme.Frame;

public class BackWinAction implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		if (Main.account.isAuth())
			Main.frame.changeWindow(Frame.WINDOW_MAIN);
		else
			Main.frame.changeWindow(Frame.WINDOW_LOGIN);
	}

}
