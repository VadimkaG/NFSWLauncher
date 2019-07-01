package ru.vadimka.nfswlauncher.theme;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import ru.vadimka.nfswlauncher.Main;

public class GUIWindowListener implements WindowListener {

	@Override
	public void windowActivated(WindowEvent arg0) {}
	@Override
	public void windowClosed(WindowEvent arg0) {}
	@Override
	public void windowClosing(WindowEvent arg0) {
		Main.shutdown(0);
	}
	@Override
	public void windowDeactivated(WindowEvent arg0) {}
	@Override
	public void windowDeiconified(WindowEvent arg0) {}
	@Override
	public void windowIconified(WindowEvent arg0) {}
	@Override
	public void windowOpened(WindowEvent arg0) {}

}
