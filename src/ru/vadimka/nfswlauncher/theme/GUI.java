package ru.vadimka.nfswlauncher.theme;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import ru.vadimka.nfswlauncher.AuthException;
import ru.vadimka.nfswlauncher.Config;
import ru.vadimka.nfswlauncher.Locale;
import ru.vadimka.nfswlauncher.Log;
import ru.vadimka.nfswlauncher.Main;
import ru.vadimka.nfswlauncher.ValueObjects.Account;
import ru.vadimka.nfswlauncher.ValueObjects.ClientLocale;
import ru.vadimka.nfswlauncher.ValueObjects.ClientSettings;
import ru.vadimka.nfswlauncher.ValueObjects.ComboboxString;
import ru.vadimka.nfswlauncher.ValueObjects.PerformanceLevel;
import ru.vadimka.nfswlauncher.ValueObjects.ServerVO;
import ru.vadimka.nfswlauncher.theme.GUIResourseLoader.Loader;
import ru.vadimka.nfswlauncher.theme.customcomponents.ButtonC;
import ru.vadimka.nfswlauncher.theme.customcomponents.DynBgC;
import ru.vadimka.nfswlauncher.theme.customcomponents.FieldC;
import ru.vadimka.nfswlauncher.theme.customcomponents.ImageC;
import ru.vadimka.nfswlauncher.theme.customcomponents.PassFieldC;
import ru.vadimka.nfswlauncher.utils.DiscordController;
import ru.vadimka.nfswlauncher.utils.HTTPRequest.Action;

public class GUI extends JFrame implements GraphModule {

	private static final long serialVersionUID = -584032146829525042L;
	private JPanel window;

	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI frame = new GUI();
					frame.loading();
					if (frame.serverList != null) {
						frame.serverList.add(new ServerVO("", "RacingWorld",false));
						frame.serverList.add(new ServerVO("","WorldEvolved",false));
						frame.serverList.add(new ServerVO("","World Online PL",false));
					}
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/
	
	private JPanel mainPanel;
	private JPanel loginPanel;
	private JPanel settingsPanel;
	
	private ButtonC btnServers;
	private ButtonC btnSettings;
	private ButtonC btnInfo;
	
	private JLabel lblOnline;
	private JTextPane descriptionText;
	private JLabel lblLogin;
	private JLabel lblServerTitle;
	private JLabel lblPing;
	private ButtonC btnStart;
	
	// Окно логина
	private JLabel login_lblOnline;
	private JTextPane login_descriptionText;
	private JLabel login_lblServerTitle;
	private JLabel login_lblPing;
	private JTextField lofinField;
	private PassFieldC passField;
	
	private JList<ServerVO> serverLV;
	private Vector<ServerVO> serverList;
	// ===========
	
	// Окно настроек
	JTextPane gameStartFileField;
	JComboBox<Locale> langField;
	// =============

	/**
	 * Create the frame.
	 */
	public GUI() {
		setSize(new Dimension(800, 510));
		setTitle(Config.WINDOW_TITLE);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setUndecorated(true);
		setResizable(false);
		window = new JPanel();
		window.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(window);
		window.setLayout(null);
		GUIMouseMovement MouseListener = new GUIMouseMovement(this,0,0,800,30);
		addMouseListener(MouseListener);
		addMouseMotionListener(MouseListener);
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent ke) {
				if (ke.getKeyCode()==KeyEvent.VK_L && ke.isAltDown())
					Log.showLogWindow();
				if (ke.getKeyCode()==KeyEvent.VK_ENTER && loading == false) {
					if (GraphActions.isAuthed()) {
						GraphActions.startGame();
					} else {
						login();
					}
					
				}
				return false;
			}
		});
		
		ButtonC btnExit = new ButtonC();
		
		btnExit.setBounds(775, 5, 20, 20);
		btnExit.setStyle(GUIResourseLoader.getStyleBtnExit());
		btnExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		window.add(btnExit);
		
		ButtonC btnHide = new ButtonC();
		btnHide.setBounds(750, 5, 20, 20);
		btnHide.setStyle(GUIResourseLoader.getStyleBtnHide());
		btnHide.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setExtendedState(JFrame.ICONIFIED);
			}
		});
		window.add(btnHide);
		
		ImageC UpPanel = new ImageC();
		UpPanel.setBackground(Color.CYAN);
		UpPanel.setImage(GUIResourseLoader.getUpBarIS(), 800, 30);
		
		JLabel lblWindowTitle = new JLabel(Config.WINDOW_TITLE/*+"    v "+Config.VERSION*/);
		lblWindowTitle.setForeground(Color.WHITE);
		lblWindowTitle.setBounds(10, 7, 203, 15);
		window.add(lblWindowTitle);
		UpPanel.setBounds(0, 0, 800, 30);
		window.add(UpPanel);
		
		btnServers = new ButtonC();
		btnServers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!loginPanel.isVisible()) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							loading();
							GraphActions.logout();
							//GraphActions.logout();
							setLogin(false);
							loadingComplite();
						}
					}).start();
				}
			}
		});
		btnServers.setBounds(15, 50, 45, 45);
		btnServers.setStyle(GUIResourseLoader.getStyleBtnServers());
		window.add(btnServers);
		
		btnSettings = new ButtonC();
		btnSettings.setBounds(15, 110, 45, 45);
		btnSettings.setStyle(GUIResourseLoader.getStyleBtnSettings());
		window.add(btnSettings);
		
		btnSettings.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loginPanel.setVisible(false);
				mainPanel.setVisible(false);
				settingsPanel.setVisible(true);
			}
		});
		
		btnInfo = new ButtonC();
		btnInfo.setBounds(15, 170, 45, 45);
		btnInfo.setStyle(GUIResourseLoader.getStyleBtnInfo());
		window.add(btnInfo);
		btnInfo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new InfoDialog().show();
			}
		});
		ImageC leftBar = new ImageC(GUIResourseLoader.getLeftBarIS(),75,480);
		leftBar.setBackground(Color.ORANGE);
		leftBar.setBounds(0, 30, 75, 480);
		window.add(leftBar);
		leftBar.setLayout(null);
		
		//*
		// Окно логина TODO
		loginPanel = new JPanel();
		loginPanel.setVisible(false);
		loginPanel.setBounds(0, 0, getWidth(), getHeight());
		window.add(loginPanel);
		loginPanel.setLayout(null);
		loginPanel.setOpaque(false);
		
		lofinField = new FieldC(Main.locale.get("label_login"));
		lofinField.setBounds(465, 294, 214, 45);
		lofinField.setEditable(true);
		lofinField.setEnabled(true);
		loginPanel.add(lofinField);
		lofinField.setColumns(10);
		
		passField = new PassFieldC();
		passField.setBounds(465, 350, 214, 45);
		loginPanel.add(passField);

		serverList = new Vector<ServerVO>();
		
		ButtonC logBtn = new ButtonC(GraphActions.getLocale().get("btn_auth_submit"));
		logBtn.setBounds(550, 406, 166, 39);
		logBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				login();
			}
		});
		
		logBtn.setStyle(GUIResourseLoader.getStyleBtn());
		loginPanel.add(logBtn);
		
		ButtonC regBtn = new ButtonC(GraphActions.getLocale().get("btn_reg_submit"));
		regBtn.setBounds(350, 406, 166, 39);
		regBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (GraphActions.getCurrentServer() == null) {
					infoDialog(GraphActions.getLocale().get("choose_server"), GraphActions.getLocale().get("auth_error_title"));
					return;
				}
				loading();
				if (passField.getPassword().length < 1 || lofinField.getText().length() < 1) {
					errorDialog(GraphActions.getLocale().get("reg_error").replace("\\n", "\n"), GraphActions.getLocale().get("reg_error_title"));
					loadingComplite();
					return;
				}
				String password = passField.getPasswordSha1();
				new Thread(() -> {
					try {
						GraphActions.getCurrentServer().getProtocol().register(lofinField.getText(), password);
						Main.frame.infoDialog(Main.locale.get("msg_reg_success"), Main.locale.get("msg_reg_success_title"));
						loadingComplite();
					} catch (AuthException ex) {
						errorDialog(Main.locale.get("msg_reg_error").replaceFirst("%%RESPONSE%%", ex.getMessage()), GraphActions.getLocale().get("msg_reg_error_title"));
						Log.getLogger().warning("Ошибка регистрации. Описание: "+ex.getMessage());
						loadingComplite();
					}
				}).start();
			}
		});
		
		regBtn.setStyle(GUIResourseLoader.getStyleBtn());
		loginPanel.add(regBtn);
		
		ButtonC forgotBtn = new ButtonC(GraphActions.getLocale().get("btn_forgot_password"));
		forgotBtn.setBounds(150, 406, 166, 39);
		forgotBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
				if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
					try {
						desktop.browse(new URI(GraphActions.getCurrentServer().getProtocol().getLinkForgotPassword()));
					} catch (Exception e1) {
						e1.printStackTrace();
						Log.getLogger().log(Level.WARNING,"",e1);
					}
				}
			}
		});
		
		forgotBtn.setStyle(GUIResourseLoader.getStyleBtn());
		loginPanel.add(forgotBtn);
		
		login_lblServerTitle = new JLabel("RacingWorld");
		login_lblServerTitle.setHorizontalAlignment(SwingConstants.CENTER);
		login_lblServerTitle.setForeground(Color.WHITE);
		login_lblServerTitle.setFont(new Font("Tahoma", Font.PLAIN, 32));
		login_lblServerTitle.setForeground(Color.WHITE);
		login_lblServerTitle.setBounds(209, 115, 469, 52);
		loginPanel.add(login_lblServerTitle);
		
		login_lblOnline = new JLabel("0/0");
		login_lblOnline.setForeground(Color.WHITE);
		login_lblOnline.setBounds(132, 221, 214, 23);
		loginPanel.add(login_lblOnline);
		
		login_lblPing = new JLabel("0 ms");
		login_lblPing.setForeground(Color.WHITE);
		login_lblPing.setBounds(132, 264, 214, 23);
		loginPanel.add(login_lblPing);
		
		login_descriptionText = new JTextPane();
		login_descriptionText.setEditable(false);
		login_descriptionText.setOpaque(false);
		login_descriptionText.setForeground(Color.WHITE);
		login_descriptionText.setEditable(false);
		login_descriptionText.setBounds(132, 307, 214, 122);
		loginPanel.add(login_descriptionText);
		
		JLabel lblAuth = new JLabel(GraphActions.getLocale().get("auth_name"));
		lblAuth.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblAuth.setHorizontalAlignment(SwingConstants.CENTER);
		lblAuth.setForeground(Color.WHITE);
		lblAuth.setBounds(100, 55, 153, 39);
		loginPanel.add(lblAuth);
		serverLV = new JList<ServerVO>(serverList);
		serverLV.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		serverLV.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (GraphActions.getCurrentServer() != null && GraphActions.getCurrentServer().getIP().equalsIgnoreCase(serverLV.getSelectedValue().getIP())) return;
				//Log.print("Выбран сервер "+serverLV.getSelectedValue().getName());
				//loading();
				serverLV.setEnabled(false);
				new Thread(() -> {
					ServerVO server = serverLV.getSelectedValue();
					server.getProtocol().getResponse(new Action() {
						@Override
						public void run() {
							SwingUtilities.invokeLater(() -> {
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {}
								updateLoginWindow();
								serverLV.setEnabled(true);
							});
						}
						@Override
						public void error() {
							updateLoginWindow();
							serverLV.setEnabled(true);
						}
					});
					GraphActions.setServer(server);
				}).start();
			}
		});
		JScrollPane serverPane = new JScrollPane(serverLV);
		serverPane.setBounds(465, 182, 214, 101);
		loginPanel.add(serverPane);
		ImageC back = new ImageC(GUIResourseLoader.getTabFirstIS(),670,430);
		back.setBounds(100, 55, 670, 430);
		loginPanel.add(back);
		
		//*/
		
		//*
		// Главное окно TODO
		mainPanel = new JPanel();
		mainPanel.setVisible(true);
		mainPanel.setBounds(0, 0, getWidth(), getHeight());
		window.add(mainPanel);
		mainPanel.setLayout(null);
		mainPanel.setOpaque(false);
		
		lblServerTitle = new JLabel("Racing World");
		lblServerTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblServerTitle.setForeground(Color.WHITE);
		lblServerTitle.setFont(new Font("Tahoma", Font.PLAIN, 32));
		lblServerTitle.setBounds(152, 82, 560, 58);
		mainPanel.add(lblServerTitle);
		
		lblLogin = new JLabel("User Login");
		lblLogin.setForeground(Color.WHITE);
		lblLogin.setBounds(482, 211, 200, 30);
		mainPanel.add(lblLogin);
		
		lblOnline = new JLabel("10/100");
		lblOnline.setForeground(Color.WHITE);
		lblOnline.setBounds(482, 243, 200, 20);
		mainPanel.add(lblOnline);
		
		lblPing = new JLabel("0 ms");
		lblPing.setForeground(Color.WHITE);
		lblPing.setBounds(482, 268, 200, 14);
		mainPanel.add(lblPing);
		
		descriptionText = new JTextPane();
		descriptionText.setEditable(false);
		descriptionText.setOpaque(false);
		descriptionText.setForeground(Color.WHITE);
		descriptionText.setText("Добро пожаловать на наш сервер, желаем вам приятной игры и хорошо повеселиться!");
		descriptionText.setBounds(162, 211, 262, 91);
		mainPanel.add(descriptionText);
		
		final ButtonC btnSite = new ButtonC("site");
		btnSite.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
				if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
					try {
						desktop.browse(new URI(GraphActions.getServerWebSite()));
					} catch (Exception e3) {
						e3.printStackTrace();
					}
				}
			}
		});
		btnSite.setBounds(234, 375, 72, 61);
		btnSite.setStyle(GUIResourseLoader.getStyleBtnSite());
		mainPanel.add(btnSite);
		
		final ButtonC btnDiscord = new ButtonC("discord");
		btnDiscord.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
				if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
					try {
						desktop.browse(new URI(GraphActions.getServerDiscord()));
					} catch (Exception e3) {
						e3.printStackTrace();
					}
				}
			}
		});
		btnDiscord.setBounds(152, 375, 72, 61);
		btnDiscord.setStyle(GUIResourseLoader.getStyleBtnDiscord());
		mainPanel.add(btnDiscord);
		
		btnStart = new ButtonC(GraphActions.getLocale().get("btn_launch_game"));
		btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GraphActions.startGame();
			}
		});
		btnStart.setBounds(532, 376, 180, 60);
		btnStart.setStyle(GUIResourseLoader.getStyleBtn());
		mainPanel.add(btnStart);
		ImageC aboutBlock = new ImageC(GUIResourseLoader.getBlockIS(),600,400);
		aboutBlock.setBackground(Color.CYAN);
		aboutBlock.setBounds(132, 56, 600, 400);
		aboutBlock.setLayout(null);
		mainPanel.add(aboutBlock);
		//*/
		
		//*
		// Настройки TODO
		settingsPanel = new JPanel();
		settingsPanel.setVisible(false);
		settingsPanel.setBounds(0, 0, getWidth(), getHeight());
		settingsPanel.setLayout(null);
		settingsPanel.setOpaque(false);
		window.add(settingsPanel);
		
		JLabel lblSettings = new JLabel(GraphActions.getLocale().get("settings_title"));
		lblSettings.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblSettings.setHorizontalAlignment(SwingConstants.CENTER);
		lblSettings.setForeground(Color.WHITE);
		lblSettings.setBounds(100, 55, 153, 39);
		settingsPanel.add(lblSettings);
		
		JLabel lblLauncherLang = new JLabel(GraphActions.getLocale().get("launcher_language"));
		lblLauncherLang.setForeground(Color.WHITE);
		lblLauncherLang.setBounds(523, 135, 189, 20);
		settingsPanel.add(lblLauncherLang);
		
		langField = new JComboBox<Locale>();
		langField.setBounds(523, 160, 189, 20);
		langField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (langField != null && !Config.LANGUAGE.equalsIgnoreCase(((Locale) langField.getSelectedItem()).getID())) {
					if (LocaleInited)
						GraphActions.changeLauncherLanguage(((Locale) langField.getSelectedItem()).getID());
				}
			}
		});
		
		settingsPanel.add(langField);
		
		gameStartFileField = new JTextPane();
		gameStartFileField.setEditable(false);
		//gameStartFileField.setHorizontalAlignment(SwingConstants.CENTER);
		StyledDocument doc = gameStartFileField.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		gameStartFileField.setFont(gameStartFileField.getFont().deriveFont(10f));
		gameStartFileField.setText(GraphActions.getLocale().get("btn_select_game_folder")+"\n"+Config.GAME_PATH);
		gameStartFileField.setBounds(523, 185, 189, 59);
		gameStartFileField.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseClicked(MouseEvent e) {
				String file = fileSelect(Config.GAME_PATH, false, GraphActions.getLocale().get("btn_select_game_folder"));
				if (file == null) return;
				gameStartFileField.setText(GraphActions.getLocale().get("btn_select_game_folder")+"\n"+file);
				if (
						!new File(file+File.separator+"nfsw.exe").exists()
						&&
						!questionDialog(GraphActions.getLocale().get("gamedir_error_file").replace("\\n", "\n"), GraphActions.getLocale().get("gamedir_error_title"))
					) {
						return;
				}
				GraphActions.setGameFilePath(file);
			}
		});
		
		settingsPanel.add(gameStartFileField);
		
		ClientSettings cset = GraphActions.getGameSettings();
		
		JLabel lblClientLang = new JLabel(GraphActions.getLocale().get("msg_client_language"));
		lblClientLang.setForeground(Color.WHITE);
		lblClientLang.setBounds(152, 135, 189, 17);
		settingsPanel.add(lblClientLang);
		
		JComboBox<ClientLocale> clientLangField = new JComboBox<ClientLocale>();
		clientLangField.setBounds(152, 160, 189, 20);
		settingsPanel.add(clientLangField);
		
		ClientLocale cl = ClientLocale.getById(cset.getLanguage());
		
		clientLangField.removeAllItems();
		
		clientLangField.addItem(cl);
		
		for (ClientLocale l : ClientLocale.values()) {
			if (!l.getId().equalsIgnoreCase(cl.getId()))
				clientLangField.addItem(l);
		}
		
		JLabel lblSoundMode = new JLabel(GraphActions.getLocale().get("msg_client_audiomode"));
		lblSoundMode.setForeground(Color.WHITE);
		lblSoundMode.setBounds(152, 191, 189, 17);
		settingsPanel.add(lblSoundMode);
		
		
		JComboBox<ComboboxString<Boolean>> soundModeField = new JComboBox<ComboboxString<Boolean>>();
		soundModeField.setBounds(152, 210, 189, 20);
		settingsPanel.add(soundModeField);
		
		if (cset.getAudiomode()) {
			soundModeField.addItem(new ComboboxString<Boolean>(GraphActions.getLocale().get("msg_client_audiomode_stereo"),true));
			soundModeField.addItem(new ComboboxString<Boolean>(GraphActions.getLocale().get("msg_client_audiomode_around"),false));
		} else {
			soundModeField.addItem(new ComboboxString<Boolean>(GraphActions.getLocale().get("msg_client_audiomode_around"),false));
			soundModeField.addItem(new ComboboxString<Boolean>(GraphActions.getLocale().get("msg_client_audiomode_stereo"),true));
		}
		
		JCheckBox smoothSoundField = new JCheckBox(GraphActions.getLocale().get("msg_client_audioquality"));
		smoothSoundField.setForeground(Color.WHITE);
		smoothSoundField.setOpaque(false);
		smoothSoundField.setBounds(148, 237, 250, 23);
		smoothSoundField.setSelected(cset.getAudioquality());
		settingsPanel.add(smoothSoundField);
		
		JLabel lblBrithness = new JLabel(GraphActions.getLocale().get("msg_client_brightness"));
		lblBrithness.setForeground(Color.WHITE);
		lblBrithness.setBounds(150, 267, 189, 17);
		settingsPanel.add(lblBrithness);
		
		JSlider brithnessField = new JSlider();
		brithnessField.setBounds(152, 284, 189, 26);
		brithnessField.setOpaque(false);
		settingsPanel.add(brithnessField);
		
		brithnessField.setValue(cset.getBrightness());
		
		JLabel lblGraphic = new JLabel(GraphActions.getLocale().get("msg_client_performance"));
		lblGraphic.setForeground(Color.WHITE);
		lblGraphic.setBounds(152, 321, 189, 17);
		settingsPanel.add(lblGraphic);
		
		JComboBox<PerformanceLevel> graphicField = new JComboBox<PerformanceLevel>();
		graphicField.setBounds(152, 340, 189, 20);
		settingsPanel.add(graphicField);
		
		PerformanceLevel cg = PerformanceLevel.getById(cset.getPerformancelevel());
		
		graphicField.addItem(cg);
		
		for (PerformanceLevel vg:PerformanceLevel.values()) {
			if (vg.getID() != cg.getID()) graphicField.addItem(vg);
		}
		
		JCheckBox vsyncField = new JCheckBox(GraphActions.getLocale().get("msg_client_vsync"));
		vsyncField.setForeground(Color.WHITE);
		vsyncField.setOpaque(false);
		vsyncField.setBounds(148, 375, 275, 23);
		vsyncField.setSelected(cset.getVsync());
		settingsPanel.add(vsyncField);
		
		JCheckBox checkBox = new JCheckBox("<html><body>"+GraphActions.getLocale().get("msg_background_work_deny")+"</body></html>");
		checkBox.setForeground(Color.WHITE);
		checkBox.setBounds(515, 285, 220, 30);
		checkBox.setOpaque(false);
		checkBox.setSelected(GraphActions.getBackgroundWork());
		settingsPanel.add(checkBox);
		
		JCheckBox settings_rserver_enable = new JCheckBox("<html><body>Перехватывать запросы</body></html>");
		settings_rserver_enable.setForeground(Color.WHITE);
		settings_rserver_enable.setBounds(515, 250, 220, 30);
		settings_rserver_enable.setOpaque(false);
		settings_rserver_enable.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				GraphActions.rserver(settings_rserver_enable.isSelected());
			}
		});
		settings_rserver_enable.setEnabled(false);
		settingsPanel.add(settings_rserver_enable);
		
		JCheckBox dont_update = new JCheckBox("<html><body>"+GraphActions.getLocale().get("update_check")+"</body></html>");
		dont_update.setForeground(Color.WHITE);
		dont_update.setBounds(515, 310, 220, 30);
		dont_update.setOpaque(false);
		dont_update.setSelected(GraphActions.getIsUpdateCheck());
		settingsPanel.add(dont_update);
		
		JCheckBox discord_integration = new JCheckBox("<html><body>"+GraphActions.getLocale().get("discord_allow")+"</body></html>");
		discord_integration.setForeground(Color.WHITE);
		discord_integration.setBounds(515, 360, 220, 30);
		discord_integration.setOpaque(false);
		discord_integration.setSelected(GraphActions.getDiscordAllow());
		settingsPanel.add(discord_integration);
		
		JCheckBox dynamic_background = new JCheckBox("<html><body>"+GraphActions.getLocale().get("dynamic_background")+"</body></html>");
		dynamic_background.setForeground(Color.WHITE);
		dynamic_background.setBounds(515, 335, 220, 30);
		dynamic_background.setOpaque(false);
		dynamic_background.setSelected(GraphActions.isDynamicBackground());
		settingsPanel.add(dynamic_background);
		
		ButtonC btnClear = new ButtonC(GraphActions.getLocale().get("clear_settings"));
		btnClear.setBounds(515, 390, 220, 30);
		btnClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GraphActions.clearLauncherSettings();
			}
		});
		settingsPanel.add(btnClear);
		
		ButtonC btnSave = new ButtonC(GraphActions.getLocale().get("btn_client_settings_save"));
		btnSave.setBounds(568, 437, 189, 39);
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (checkBox.isSelected() && discord_integration.isSelected()) discord_integration.setSelected(false);
				
				boolean discord_integration_changed = discord_integration.isSelected() != GraphActions.getDiscordAllow();
				
				boolean dybbg_changed = dynamic_background.isSelected() != GraphActions.isDynamicBackground();
				
				if (
						checkBox.isSelected() != GraphActions.getBackgroundWork() ||
						dont_update.isSelected() != GraphActions.getIsUpdateCheck() ||
						discord_integration_changed ||
						dybbg_changed
					)
					GraphActions.setLauncherSettings(checkBox.isSelected(),dont_update.isSelected(), discord_integration.isSelected(), dynamic_background.isSelected());
				
				if (discord_integration_changed) {
					if (discord_integration.isSelected()) {
						DiscordController.load();
					} else {
						DiscordController.shutdown();
					}
				}
				
				// Сохраняем язык игры
				cset.setLanguage(((ClientLocale) clientLangField.getSelectedItem()).getId());
				
				// Сохраняем режим звука
				@SuppressWarnings("unchecked")
				ComboboxString<Boolean> comboboxString = (ComboboxString<Boolean>)soundModeField.getSelectedItem();
				cset.setAudiomode(comboboxString.getValue());
				
				// Сохраняем "Более качественный звук"
				cset.setAudioquality(smoothSoundField.isSelected());
				
				// Сохраняем яркость
				cset.setBrightness(brithnessField.getValue());
				
				// Сохраняем пресет графики
				cset.setPerformancelevel(((PerformanceLevel)graphicField.getSelectedItem()).getID());
				
				// Сохраняем вертикальную синхронизацию
				cset.setVsync(vsyncField.isSelected());
				
				// Записываем настройки
				GraphActions.setGameSettings(cset);
				
				if (dybbg_changed) {
					Main.restart();
					return;
				}
				
				// Закрываем настройки
				loading();
				loadingComplite();
			}
		});

		btnSave.setStyle(GUIResourseLoader.getStyleBtn());
		settingsPanel.add(btnSave);
		
		ButtonC btnCancel = new ButtonC(GraphActions.getLocale().get("btn_back"));
		btnCancel.setBounds(369, 437, 189, 39);
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loading();
				loadingComplite();
			}
		});


		btnCancel.setStyle(GUIResourseLoader.getStyleBtn());
		settingsPanel.add(btnCancel);
		ImageC sback = new ImageC(GUIResourseLoader.getTabFirstIS(),670,430);
		sback.setBounds(100, 55, 670, 430);
		settingsPanel.add(sback);
		//*/
		
		Component backgroundMapPanel;
		
		ImageC backgroundPanel = new ImageC(GUIResourseLoader.getBackgroundIS(),getWidth(),getHeight());
		
		if (GraphActions.isDynamicBackground())
			backgroundMapPanel = new DynBgC(GUIResourseLoader.getMapIS(),2047,1503);
		else
			backgroundMapPanel = new ImageC(GUIResourseLoader.getMapIS(),725,480);
		backgroundMapPanel.setBounds(75, 30, 725, 480);
		window.add(backgroundMapPanel);
		backgroundPanel.setBounds(0, 0, getWidth(), getHeight());
		window.add(backgroundPanel);
		
		//*
		GUIResourseLoader.loadIcon(new Loader<Image>() {
			@Override
			public void proc(Image obj) {
				setIconImage(obj);
			}
		});
		
		loading();
	}
	
	private boolean loading = false;

	@Override
	public void loading() {
		
		loading = true;
		
		btnStart.setEnabled(false);
		btnServers.setEnabled(false);
		btnSettings.setEnabled(false);
		btnInfo.setEnabled(false);
		
		//*
		loginPanel.setVisible(false);
		settingsPanel.setVisible(false);
		mainPanel.setVisible(true);
		//*/
		
		//* Главное окно
		lblServerTitle.setText(GraphActions.getLocale().get("loading"));
		descriptionText.setText("");
		lblLogin.setText("");
		lblPing.setText(GraphActions.getLocale().get("ping"));
		lblOnline.setText(GraphActions.getLocale().get("server_online"));
		// ============*/
		
		//* Окно логина
		login_lblServerTitle.setText(GraphActions.getLocale().get("loading"));
		login_descriptionText.setText("");
		login_lblPing.setText(GraphActions.getLocale().get("ping"));
		login_lblOnline.setText(GraphActions.getLocale().get("server_online"));
		// ==============*/
	}
	@Override
	public void loadingComplite() {
		
		loading = false;
		
		if (isLogged) {
			//* 
			lblServerTitle.setText(GraphActions.getServerName());
			descriptionText.setText(GraphActions.getServerDescription());
			lblLogin.setText(GraphActions.getUserName());
			
			btnStart.setEnabled(true);
			btnServers.setEnabled(true);
			btnSettings.setEnabled(true);
			btnInfo.setEnabled(true);
			// ============*/
			
		} else {
			//*
			passField.setText("");
			login_lblServerTitle.setText(GraphActions.getServerName());
			login_descriptionText.setText(GraphActions.getServerDescription());
			lofinField.setText(GraphActions.getUserName());
			//*/
			new Thread(new Runnable() {
				@Override
				public void run() {
					List<ServerVO> servers = GraphActions.getServerList();
					
					boolean isTry = true;
					while (servers.isEmpty() && isTry == true) {
						isTry = questionDialog(Main.locale.get("servers_empty"), Main.locale.get("servers_empty_title"));
						if (isTry == true) servers = GraphActions.getServerList();
					}
					
					updateServers(servers);
					
					mainPanel.setVisible(false);
					loginPanel.setVisible(true);
					
					serverLV.setEnabled(true);
					btnStart.setEnabled(true);
					btnServers.setEnabled(true);
					btnSettings.setEnabled(true);
					btnInfo.setEnabled(true);
				}
			}).start();
		}
		startPinging();
	}
	/**
	 * Запустить цикловой пинг сервера
	 */
	public void startPinging() {
		if (CHECKER != null) {
			CHECKER.cancel();
			CHECKER = null;
		}
		if (GraphActions.getCurrentServer() != null) {
			CHECKER = new Timer();
			CHECKER.schedule(new TimerTask() {
				@Override
				public void run() {
					GraphActions.getCurrentServer().getProtocol().ping();
					updateStats();
				}
			}, 10, 3000);
		}
	}
	/**
	 * Обновить информацию о сервере в окне логина
	 */
	private void updateLoginWindow() {
		login_lblServerTitle.setText(GraphActions.getServerName());
		login_descriptionText.setText(GraphActions.getServerDescription());
		startPinging();
	}
	
	private Timer CHECKER = null;
	
	private void updateStats() {
		if (GraphActions.getCurrentServer().getProtocol().isOnline()) {
			
			//* Главное окно
			lblPing.setText(GraphActions.getLocale().get("ping")+GraphActions.getCurrentServer().getProtocol().get("ping")+" ms");
			lblOnline.setText(GraphActions.getLocale().get("server_online")+GraphActions.getServerOnline()+"/"+GraphActions.getServerOnlineMax());
			// ===========*/
			
			//* Окно логина
			login_lblPing.setText(GraphActions.getLocale().get("ping")+GraphActions.getCurrentServer().getProtocol().get("ping")+" ms");
			login_lblOnline.setText(GraphActions.getLocale().get("server_online")+GraphActions.getServerOnline()+"/"+GraphActions.getServerOnlineMax());
			// ============*/
		} else {
			
			//* Главное окно
			lblPing.setText(GraphActions.getLocale().get("ping")+"не отвечает");
			lblOnline.setText(GraphActions.getLocale().get("server_offline"));
			// ============*/
			
			//* Окно логина
			login_lblPing.setText(GraphActions.getLocale().get("ping")+"не отвечает");
			login_lblOnline.setText(GraphActions.getLocale().get("server_offline"));
			// =============*/
		}
	}
	
	private boolean isLogged = false;
	
	@Override
	public void setLogin(boolean b) {
		isLogged = b;
	}
	@Override
	public void errorDialog(String text, String title) {
		JOptionPane.showMessageDialog(this, text, title, javax.swing.JOptionPane.ERROR_MESSAGE, null);
	}
	@Override
	public void infoDialog(String text, String title) {
		JOptionPane.showMessageDialog(this, text, title, javax.swing.JOptionPane.INFORMATION_MESSAGE, null);
	}
	@Override
	public boolean questionDialog(String text, String title) {
		Object[] options = { GraphActions.getLocale().get("yes"), GraphActions.getLocale().get("no") };
		Integer a = JOptionPane.showOptionDialog(this, text,
				title, JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		if (a == 0) return true;
		else return false;
	}
	@Override
	public String fileSelect(String path, boolean itsFile) {
		return fileSelect(path, itsFile, GraphActions.getLocale().get("btn_file_select"));
	}
	public String fileSelect(String path, boolean itsFile, String title) {
		String filePath = "";
		JFileChooser filechooser;
		if (path == null)
			filechooser = new JFileChooser();
		else
			filechooser = new JFileChooser(path);
		if (itsFile) filechooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		else  filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		filechooser.setDialogTitle(title);
		int ret = filechooser.showDialog(null, GraphActions.getLocale().get("btn_file_select")); 
		if (ret == JFileChooser.APPROVE_OPTION) {
			File file = filechooser.getSelectedFile();
			filePath = file.getAbsolutePath();
			return filePath;
		} else return null;
	}
	@Override
	public void updateServers(List<ServerVO> servers) {
		serverList.clear();
		for (ServerVO serv : servers) {
			serverList.add(serv);
		}
		serverLV.updateUI();
	}
	private boolean LocaleInited = false;
	@Override
	public void updateLocales(Locale[] locales) {
		langField.addItem(GraphActions.getLocale());
		for (Locale loc : locales) {
			if (!loc.getID().equalsIgnoreCase(GraphActions.getLocale().getID()))
				langField.addItem(loc);
		}
		LocaleInited = true;
	}

	@Override
	public void destroy() {
		setVisible(false);
		dispose();
	}
	@Override
	public void setDownloadState(boolean Status) {
		setDownloadState(Status, 0);
	}
	@Override
	public void setDownloadState(boolean Status, int MaxValue) {
		if (Status) {
			current_files_loaded = 0;
			max_files_loaded = MaxValue;
			lblServerTitle.setText(
					GraphActions.getLocale().get("loading_files").replace("%MAX%",String.valueOf(max_files_loaded))
						.replace("%CUR%", String.valueOf(current_files_loaded))
				);
		} else {
			lblServerTitle.setText(GraphActions.getLocale().get("loading"));
		}
	}
	private int current_files_loaded = 0;
	private int max_files_loaded = 0;
	@Override
	public void DownloadStateAddValue() {
		current_files_loaded++;
		lblServerTitle.setText(
				GraphActions.getLocale().get("loading_files").replace("%MAX%",String.valueOf(max_files_loaded))
					.replace("%CUR%", String.valueOf(current_files_loaded))
			);
	}
	private void login() {
		if (GraphActions.getCurrentServer() == null) {
			infoDialog(GraphActions.getLocale().get("choose_server"), GraphActions.getLocale().get("auth_error_title"));
			return;
		}
		loading();
		String password = passField.getPasswordSha1();
		ServerVO server = GraphActions.getCurrentServer();
		Account acc = new Account(server, lofinField.getText(), password);

		try {
			server.getProtocol().login(acc);
			GraphActions.login(acc);
			setLogin(true);
			loadingComplite();
		} catch (NullPointerException exn) {
			errorDialog(GraphActions.getLocale().get("error_inner"), GraphActions.getLocale().get("auth_error_title"));
			Log.getLogger().log(Level.WARNING,"Внутренняя ошибка авторизации, не удалось получить данные.",exn);
			//Log.getLogger().warning(exn.getStackTrace());
			//Main.frame.changeWindow(Frame.WINDOW_LOGIN);
			setLogin(false);
			loadingComplite();
		} catch (AuthException ex) {
			errorDialog(Main.locale.get("auth_error").replaceFirst("%%RESPONSE%%", ex.getMessage()), GraphActions.getLocale().get("auth_error_title"));
			Log.getLogger().warning("Ошибка логина. Описание: "+ex.getMessage());
			setLogin(false);
			loadingComplite();
		}
	}
}
