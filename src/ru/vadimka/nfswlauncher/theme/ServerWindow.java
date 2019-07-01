package ru.vadimka.nfswlauncher.theme;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

import ru.vadimka.nfswlauncher.Log;

public class ServerWindow extends JFrame {

	private static final long serialVersionUID = -4700918479395886619L;
	
	private JPanel contentPane;
	private static JTextPane logText;
	private JButton btnStart;
	private JButton btnStop;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerWindow frame = new ServerWindow();
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
	public ServerWindow() {
		setTitle("Сервер-менеджер");
		setBounds(100, 100, 500, 350);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		
		btnStart = new JButton("Старт");
		contentPane.add(btnStart);
		btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//RedirServerCore.start();
			}
		});
		
		btnStop = new JButton("Стоп");
		contentPane.add(btnStop);
		btnStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//RedirServerCore.stop();
			}
		});
		
		logText = new JTextPane();
		logText.setEditable(false);
		contentPane.add(logText);
		
		JScrollPane scrollPane = new JScrollPane(logText);
		contentPane.add(scrollPane);
	}
	
	public static class LogWindowHandler extends Log.LogHandler {
		
		@Override
		public void print(String message) {
			logText.setText(logText.getText()+message+"\n");
		}
	}
	
	private static ServerWindow INSTANCE = null;
	
	public static void showInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ServerWindow();
		}
		INSTANCE.setVisible(true);
	}
}
