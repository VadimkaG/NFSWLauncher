package ru.vadimka.nfswlauncher.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ru.vadimka.nfswlauncher.Config;
import ru.vadimka.nfswlauncher.Game;
import ru.vadimka.nfswlauncher.Log;
import ru.vadimka.nfswlauncher.Main;
import ru.vadimka.nfswlauncher.utils.RWAC;

public class StartAction implements ActionListener {
	
	ActionListener AChangeClient = new ChangeClientAction();

	@Override
	public void actionPerformed(ActionEvent e) {
		if (Main.getPlatform() != "Winows") {
			if (Main.getPlatform() == "Unix") {
				if (Config.WINE_PATH.equalsIgnoreCase("")) {
					Main.frame.showDialog(
							"Похоже вы играете на unix... Для этого вам нужно пройти в ~/Need For Speed World/launcher.cfg\n"
							+ "и добавить две новые конфигураци:\n"
							+ "winepath - путь к запуску wine\n"
							+ "wineprefix - префикс wine, через которого будете запускать игру"
							, Main.locale.get("launch_error_title"));
				return;
			} else
					Log.print("Запуск игры в режиме wine...");
			} else if (Main.getPlatform() != "Unix") {
				Main.frame.showDialog("Похоже ваша платформа не поддерживается лаунчером.\nСожалеем, но дальше вам не пройти", Main.locale.get("launch_error_title"));
				Log.print("Платформа "+Main.getPlatform()+" не поддерживается. Запуск отменен.");
				return;
			}
		}
		if (Config.GAME_PATH.equalsIgnoreCase("")) {
			Main.frame.showDialog(Main.locale.get("launch_error_file_not_choosed"), Main.locale.get("launch_error_title"));
			AChangeClient.actionPerformed(new ActionEvent(0, 0, ""));
		} else {
			if (RWAC.checkBeforeStart())
				Main.game = new Game(Main.account.getToken(), Main.account.getID(), Main.account.getServer().getProtocol().getServerEngine(), Config.GAME_PATH);
			else {
				Main.frame.showDialog("Запуск модифицированных клиентов запрещен!", Main.locale.get("launch_error_title"));
			}
		}
	}

}
