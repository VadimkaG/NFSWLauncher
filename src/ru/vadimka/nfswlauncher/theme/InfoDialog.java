package ru.vadimka.nfswlauncher.theme;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ru.vadimka.nfswlauncher.Config;
import ru.vadimka.nfswlauncher.Main;

public class InfoDialog {
	
	public static final String DESIGN_VERSION = "1.0";

	public InfoDialog() {}
	
	public void show() {
		JOptionPane.showMessageDialog((JFrame) Main.frame, getLocalizedMessage(),getLocalizedTitle(),JOptionPane.INFORMATION_MESSAGE,null);
	}
	
	private String getLocalizedMessage() {
		switch(Main.locale.getID()) {
		case "ru":
			return "Версия лаунчера: "+Config.VERSION+"\n"+
			"Версия дизайна: "+DESIGN_VERSION+"\n"+
			"Разработчик лаунчера: Vadimka - vadik.golubeff@yandex.ru\n"+
			"Автор дизайна: Ryan Cooper - Discord: Ryan Cooper (Cooperyan)#9057\n"+
			"Что изменилось в этой версии:\n"+
			"- Обновлен RacingWorld Anti-Cheat (RWAC)\n"+
			"- Исправлено условие определяющее версию анти-чита";
		default:
			return "Launcher version: "+Config.VERSION+"\n"+
			"Design version: "+DESIGN_VERSION+"\n"+
			"Launcher developer: Vadimka - vadik.golubeff@yandex.ru\n"+
			"Design author: Ryan Cooper - Discord: Ryan Cooper (Cooperyan)#9057\n"+
			"What has changed in this version:\n"+
			"- Updated RacingWorld Anti-Cheat (RWAC)\n"+
			"- Fixed condition defining version of anti-cheat";
		}
	}
	
	private String getLocalizedTitle() {
		switch(Main.locale.getID()) {
		case "ru":
			return "О лаунчере";
		default:
			return "About launcher";
		}
	}

}
