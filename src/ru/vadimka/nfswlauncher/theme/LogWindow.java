package ru.vadimka.nfswlauncher.theme;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

import ru.vadimka.nfswlauncher.Config;
import ru.vadimka.nfswlauncher.Log;

public class LogWindow extends JFrame {

	private static final long serialVersionUID = -4700918479395886619L;
	
	private JPanel contentPane;
	private JTextPane descriptionPane;
	private static JTextPane logText;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LogWindow frame = new LogWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public LogWindow() {
		setTitle("Лог-менеджер");
		setBounds(100, 100, 500, 350);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		
		descriptionPane = new JTextPane();
		descriptionPane.setEditable(false);
		contentPane.add(descriptionPane);
		
		logText = new JTextPane();
		logText.setEditable(false);
		contentPane.add(logText);
		
		JScrollPane scrollPane = new JScrollPane(logText);
		contentPane.add(scrollPane);
		updateLog();
	}
	/**
	 * Установить описание
	 * @param description - Описание
	 */
	public LogWindow setDescription(String description) {
		descriptionPane.setText(description);
		return this;
	}
	
	public void updateLog() {
		if (!Config.MODE_LOG_FILE) return;
		try {
			File file = new File(Log.getLogFilePath());
			BufferedReader br = new BufferedReader(new FileReader(file));
			
			String line = "";
			String allFile = "";
			
			while((line = br.readLine()) != null) {
				allFile += line+"\n";
			}
			br.close();
			logText.setText(allFile);
			allFile = null;
		} catch (Exception e) {
			logText.setText("ОШИБКА: Не удалось прочитать файл лога\nОписание ошибки: "+e.getMessage());
		}
	}
	
	public static class LogWindowHandler extends Log.LogHandler {
		
		@Override
		public void print(String message) {
			logText.setText(logText.getText()+message+"\n");
		}
	}
}
