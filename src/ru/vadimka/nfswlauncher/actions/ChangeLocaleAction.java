package ru.vadimka.nfswlauncher.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ru.vadimka.nfswlauncher.Config;
import ru.vadimka.nfswlauncher.Main;
import ru.vadimka.nfswlauncher.locales.Locale;
import ru.vadimka.nfswlauncher.theme.Frame;
import ru.vadimka.nfswlauncher.theme.customcomponents.ComboBoxC;

public class ChangeLocaleAction implements ActionListener{

	private static boolean inited = false;
	
	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (!inited) return;
		ComboBoxC<Locale> locs = null;
		
		if (Main.frame.getWindow(Frame.WINDOW_SETTINGS).getComponent("LanguageList") instanceof ComboBoxC<?>)
			locs = (ComboBoxC<Locale>) Main.frame.getWindow(Frame.WINDOW_SETTINGS).getComponent("LanguageList");
		
		if (locs != null && !Config.LANGUAGE.equalsIgnoreCase(((Locale) locs.getSelectedItem()).getID())) {
			Config.LANGUAGE = ((Locale) locs.getSelectedItem()).getID();
			Config.save();
			Main.restart();
		}
	}
	
	public static void on() {
		inited = true;
	}

}
