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
			"- Небольшие фиксы в алгоритме запуска игры.\n"+
			"- Убран запуск игры по Enter из-за бага с рассинхроном\n  и пропуска ошибок авторизации.\n"+
			"- Изменен параметр обнаружения RWAC (тонкая подстройка под\n  новые версии сервера WorldEvolved).\n";
		default:
			return "Launcher version: "+Config.VERSION+"\n"+
			"Design version: "+DESIGN_VERSION+"\n"+
			"Launcher developer: Vadimka - vadik.golubeff@yandex.ru\n"+
			"Design author: Ryan Cooper - Discord: Ryan Cooper (Cooperyan)#9057\n"+
			"What has changed in this version:\n"+
			"- Minor fixes in the game launch algorithm.\n"+
			"- Removed game launch by Enter due to a bug with desynchronization\n and skipping authorization errors\n"+
			"- Changed RWAC detection parameter (fine-tuning for\n new versions of WorldEvolved server).\n";
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
