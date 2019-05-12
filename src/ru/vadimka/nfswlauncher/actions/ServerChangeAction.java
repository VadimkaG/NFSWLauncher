package ru.vadimka.nfswlauncher.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ru.vadimka.nfswlauncher.Config;
import ru.vadimka.nfswlauncher.Main;
import ru.vadimka.nfswlauncher.ValueObjects.ServerVO;
import ru.vadimka.nfswlauncher.theme.Frame;
import ru.vadimka.nfswlauncher.theme.customcomponents.ComboBoxC;

public class ServerChangeAction implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (Main.frame.getWindow(Frame.WINDOW_LOGIN).getComponent("ServersList") instanceof ComboBoxC<?>) {
				@SuppressWarnings("unchecked")
				ComboBoxC<ServerVO> ServesComboBox = (ComboBoxC<ServerVO>) Main.frame.getWindow(Frame.WINDOW_LOGIN).getComponent("ServersList");
				Main.server = (ServerVO) ServesComboBox.getSelectedItem();
				Config.saveServer(Main.server.getName(), Main.server.getIP(),Main.server.getProtocol().getNameProtocol());
				Main.server.getProtocol().getResponse();
				Main.frame.updateServerInfo();
			}
		} catch (NullPointerException ex) {}
	}

}
