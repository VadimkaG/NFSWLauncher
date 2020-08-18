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
			"- Добавлен вывод лога игры в лог лаунчера\n"+
			"- Добавлена кнопка очистки настроек лаунчера\n"+
			"- Добавлен кэш серверов.\n"+
			"  Теперь если онлайн список серверов будет не доступен\n"+
			"  список серверов будет подгружен из сохраненного ранее списка.\n"+
			"- В английском переводе добавлены некоторые фразы,\n"+
			"  которые ранее не были переведены\n"+
			"- Теперь WINE выводит отладку в лог лаунчера.\n"+
			"- Обновлен RWAC. Теперь он перепроверяет файлы тщательнее.\n"+
			"  Не шалите, играйте честно.";
		default:
			return "Launcher version: "+Config.VERSION+"\n"+
			"Design version: "+DESIGN_VERSION+"\n"+
			"Launcher developer: Vadimka - vadik.golubeff@yandex.ru\n"+
			"Design author: Ryan Cooper - Discord: Ryan Cooper (Cooperyan)#9057\n"+
			"What has changed in this version:\n"+
			"- Added output of the game log to the launcher log\n"+
			"- Added button that clears launcher settings\n"+
			"- Added server cache.\n"+
			"  Now if the online server list is not available\n"+
			"  the list of servers will be loaded from a previously saved list.\n"+
			"- In the English translation, some messages have been added,\n"+
			"  that were not previously translated\n"+
			"- Now WINE displays debugging in the launcher log.\n"+
			"- Updated RWAC. Now he double-checks the files more carefully.\n"+
			"  Don't be naughty, play fair";
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
