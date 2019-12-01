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
			"- Теперь лаунчер создает файл игровых настроек, если он не существует\n"+
			"- Отныне на WINE тоже работает изменение настроек игры.\n"+
			"- Теперь лаунчер в конфиге хранит путь директории игры,\n"+
			"  а не путь к файлу запуска. Вам придется выбрать путь к игре заного.\n"+
			"- Теперь после обновлений автоматически будет появляться окно 'О лаунчере'\n"+
			"  Эффект будет виден только со следующего обновления.";
		default:
			return "Launcher version: "+Config.VERSION+"\n"+
			"Design version: "+DESIGN_VERSION+"\n"+
			"Launcher developer: Vadimka - vadik.golubeff@yandex.ru\n"+
			"Design author: Ryan Cooper - Discord: Ryan Cooper (Cooperyan)#9057\n"+
			"What has changed in this version:\n"+
			"- Launcher creates a game settings file if it does not exist\n"+
			"- Changing the game settings also works on WINE.\n"+
			"- The launcher in the config stores the path of the game directory,\n"+
			"  and not the path to the launch file. You will have to choose the path to the game.\n"+
			"- Now after the updates the 'About Launcher' window will automatically appear\n"+
			"  The effect will be visible only from the next update.";
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
