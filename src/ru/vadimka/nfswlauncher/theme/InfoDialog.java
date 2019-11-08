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
			"-  Теперь лаунчер будет чуть более адекватно реагировать на сервера в оффлайн.\n"+
			"- Теперь RWAC может удалять файлы игры по требованию сервера.\n"+
			"  Файлы не удалятся на всегда. Как и положено они откопируются в каталог backup\n" +
			"  и восстановятся, если вы будете играть на серверах без RWAC\n" +
			"- Слегка оптимизирован RWAC. Поправлены незначительные баги"+
			"- Пофикшен баг из-за которого RWAC отказывался создавать директории";
		default:
			return "Launcher version: "+Config.VERSION+"\n"+
			"Design version: "+DESIGN_VERSION+"\n"+
			"Launcher developer: Vadimka - vadik.golubeff@yandex.ru\n"+
			"Design author: Ryan Cooper - Discord: Ryan Cooper (Cooperyan)#9057\n"+
			"What has changed in this version:\n"+
			"- Now the launcher will respond a little more adequately to servers offline.\n"+
			"- RWAC can now delete game files on server request.\n"+
			"  Files will not be deleted forever. As expected, they are copied to backup dir\n" +
			"  and recover if you play on servers without RWAC\n" +
			"- Slightly optimized RWAC. Minor bugs fixed"+
			"- Fixed a bug due to which RWAC refused to create directories";
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
