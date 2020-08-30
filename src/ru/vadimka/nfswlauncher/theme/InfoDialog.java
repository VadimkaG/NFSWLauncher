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
			"- Изменен алгоритм слияния онлайн списка сервера и кэша.\n"+
			"  Теперь он будет работать правильнее и чуть быстрее.\n"+
			"- Добавлены кнопки управления кэшем списка серверов.\n"+
			"  Тепер вы можете добавить свой сервер в список.\n"+
			"  Обратите внимание, что добавленый сервер видите только вы.\n"+
			"  Также у вас не удастся удалить сервера из онлайн списка.\n"+
			"  'Онлайн' список проверяется каждый раз при открытии окна авторизации.\n";
		default:
			return "Launcher version: "+Config.VERSION+"\n"+
			"Design version: "+DESIGN_VERSION+"\n"+
			"Launcher developer: Vadimka - vadik.golubeff@yandex.ru\n"+
			"Design author: Ryan Cooper - Discord: Ryan Cooper (Cooperyan)#9057\n"+
			"What has changed in this version:\n"+
			"- Added output of the game log to the launcher log\n"+
			"- Added button that clears launcher settings\n"+
			"- Added server cache.\n"+
			"- Changed the algorithm for merging the online server list and cache.\n"+
			"  Now it will work more correctly and a little faster.\n"+
			"- Added buttons for managing the server list cache.\n"+
			"  Now you can add your server to the list.\n"+
			"  Please note that only you can see the added server.\n"+
			"  Attention! You cannot remove servers from the online list.\n"+
			"  The 'online' list is checked every time the login window is opened.\n";
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
