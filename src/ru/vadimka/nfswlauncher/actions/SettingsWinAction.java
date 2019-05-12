package ru.vadimka.nfswlauncher.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ru.vadimka.nfswlauncher.Main;
import ru.vadimka.nfswlauncher.theme.Frame;

public class SettingsWinAction implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		Main.frame.changeWindow(Frame.WINDOW_SETTINGS);
	}

}
