package ru.vadimka.nfswlauncher.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;

import ru.vadimka.nfswlauncher.Config;
import ru.vadimka.nfswlauncher.Game;
import ru.vadimka.nfswlauncher.Main;

public class ChangeClientAction implements ActionListener {
	
	JFileChooser filechooser = new JFileChooser();

	@Override
	public void actionPerformed(ActionEvent e) {
		int ret = filechooser.showDialog(null, Main.locale.get("btn_change_file_game")); 
		if (ret == JFileChooser.APPROVE_OPTION) {
			File file = filechooser.getSelectedFile();
			if (Game.isLaunchFile(file.getAbsolutePath())) {
				Config.GAME_PATH = file.getAbsolutePath();
				//FilePathLabel.setText(Config.GAME_PATH);
				Config.save();
			} else
				Main.frame.showDialog(Main.locale.get("launch_error_file"), Main.locale.get("launch_error_title"));
		}
	}

}
