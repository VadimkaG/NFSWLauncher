package ru.vadimka.nfswlauncher.theme;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URI;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ru.vadimka.nfswlauncher.Config;
import ru.vadimka.nfswlauncher.Log;
import ru.vadimka.nfswlauncher.Main;
import ru.vadimka.nfswlauncher.ValueObjects.ComboboxString;
import ru.vadimka.nfswlauncher.ValueObjects.ServerVO;
import ru.vadimka.nfswlauncher.actions.BackWinAction;
import ru.vadimka.nfswlauncher.actions.ChangeClientAction;
import ru.vadimka.nfswlauncher.actions.ChangeLocaleAction;
import ru.vadimka.nfswlauncher.actions.ChangeUserAction;
import ru.vadimka.nfswlauncher.actions.ClientConfigAction;
import ru.vadimka.nfswlauncher.actions.FormLoginAction;
import ru.vadimka.nfswlauncher.actions.FormRegAction;
import ru.vadimka.nfswlauncher.actions.ServerChangeAction;
import ru.vadimka.nfswlauncher.actions.SettingsWinAction;
import ru.vadimka.nfswlauncher.actions.StartAction;
import ru.vadimka.nfswlauncher.locales.Locale;
import ru.vadimka.nfswlauncher.theme.customcomponents.ButtonC;
import ru.vadimka.nfswlauncher.theme.customcomponents.CheckBoxC;
import ru.vadimka.nfswlauncher.theme.customcomponents.ComboBoxC;
import ru.vadimka.nfswlauncher.theme.customcomponents.DynBgC;
import ru.vadimka.nfswlauncher.theme.customcomponents.FieldC;
import ru.vadimka.nfswlauncher.theme.customcomponents.ImageC;
import ru.vadimka.nfswlauncher.theme.customcomponents.LabelC;
import ru.vadimka.nfswlauncher.theme.customcomponents.PassFieldC;
import ru.vadimka.nfswlauncher.theme.customcomponents.SliderC;
import ru.vadimka.nfswlauncher.theme.manager.ClientSettingsTheme;
import ru.vadimka.nfswlauncher.theme.manager.LoginTheme;
import ru.vadimka.nfswlauncher.theme.manager.MainTheme;
import ru.vadimka.nfswlauncher.theme.manager.RegisterTheme;
import ru.vadimka.nfswlauncher.theme.manager.SettingsTheme;
import ru.vadimka.nfswlauncher.theme.manager.ThemeItemNode;

public class Frame extends JFrame implements MouseListener, MouseMotionListener, WindowListener {
	private static final long serialVersionUID = 1L;
	
	//private LabelC VersionLabel = new LabelC(Main.locale.get("launcher_version"));
	private LabelC VersionField = new LabelC();
	
	private ButtonC CloseButton;
	private ButtonC HideButton;
	
	private HashMap<Integer,Window> WINDOWS;
	
	public static final int WINDOW_LOADING = 0;
	public static final int WINDOW_MAIN = 1;
	public static final int WINDOW_LOGIN = 2;
	public static final int WINDOW_REGISTER = 3;
	public static final int WINDOW_SETTINGS = 4;
	public static final int WINDOW_CLIENT_SETTINGS = 5;
	public static final int WINDOW_ABOUT = 6;
	
	private Point clickPoint;
	
	private int CURRENT_WINDOW;
	
	@SuppressWarnings("unchecked")
	public Frame() {
		getContentPane().setLayout(null);
		setTitle(Config.WINDOW_TITLE);
		setSize(800, 510);
		setResizable(false);
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(this);
		setUndecorated(true);
		
		try {
			setIconImage(ImageIO.read(Frame.class.getResourceAsStream("icon.png")));
		} catch (Exception e1) {
			Log.print("Не возможно загрузить иконку: "+e1.getLocalizedMessage());
		}
		
		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((int) (screenSize.getWidth() - getWidth()) / 2, (int) (screenSize.getHeight() - getHeight()) / 2);
		
		MainTheme.load();
		
		// Кнопка "Закрыть"
		CloseButton = new ButtonC("");
		CloseButton.setStyle(MainTheme.NODE_BUTTON_CLOSE.getStyle());
		CloseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.shutdown(0);
			}
		});
		
		// Кнопка "Свернуть"
		HideButton = new ButtonC("");
		HideButton.setStyle(MainTheme.NODE_BUTTON_HIDE.getStyle());
		HideButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setExtendedState(JFrame.ICONIFIED);
			}
		});
		
		addMouseListener(this);
		addMouseMotionListener(this);

		addComponent(CloseButton, getWidth()-21, 1, 20, 20);
		addComponent(HideButton, getWidth()-42, 1, 20, 20);
		
		WINDOWS = new HashMap<Integer,Window>();

		createWindow(WINDOW_LOADING)
			//.addLayout()
			.addComponent("Text", new LabelC(Main.locale.get("loading")), MainTheme.NODE_TEXT_LOADING);
		
		// Главное окно
		createWindow(WINDOW_MAIN)
			//.addLayout()
			.addComponent("InfoLabel", new LabelC(),MainTheme.NODE_INFO_LABEL)
			.addComponent("ChangeUserButton", new ButtonC(Main.locale.get("btn_change_user"), new ChangeUserAction()), MainTheme.NODE_BUTTON_CHANGE)
			.addComponent("StartButton", new ButtonC(Main.locale.get("btn_launch_game"), new StartAction()), MainTheme.NODE_BUTTON_START)
			.addComponent("SettingsButton", new ButtonC(Main.locale.get("btn_settings"), new SettingsWinAction()), MainTheme.NODE_BUTTON_SETTINGS)
			.addComponent("Banner", new ImageC(), MainTheme.NODE_BANNER)
			.addComponent("BannerBlock", new ImageC(MainTheme.BLOCK_FILE), MainTheme.NODE_BANNER_BLOCK)
			.addComponent("BlockInfo", new ImageC(MainTheme.BLOCK_FILE), MainTheme.NODE_BLOCK_INFO)
			.setUpdateFunc(() -> {
				((ImageC) WINDOWS.get(WINDOW_MAIN).getComponent("Banner")).setURL(Main.server.getProtocol().get("BANNER"));
				Window instance = WINDOWS.get(WINDOW_MAIN);
				if (instance.getComponent("InfoLabel") instanceof LabelC) {
					LabelC InfoLabel = (LabelC) instance.getComponent("InfoLabel");
					InfoLabel.setText("<html><table>"+
							"<tr><td>"+Main.locale.get("your_login")+"</td><td>"+Config.USER_LOGIN+"</td></tr>"
							+"<tr><td>"+Main.locale.get("server_name")+"</td><td>"+Main.server.getProtocol().get("SERVER_NAME")+"</td></tr>"
							+"<tr><td>"+Main.locale.get("server_online")+"</td><td>"+Main.server.getProtocol().get("PLAYERS_ONLINE")+"/"+Main.server.getProtocol().get("PLAYERS_MAX")+"</td></tr>"
							+"<tr><td>Discord:    </td><td>"+Main.server.getProtocol().get("DISCORD")+"</td></tr>"
							+"<tr><td>Веб-сайт:</td><td>"+Main.server.getProtocol().get("WEB_SITE")+"</td></tr>"
							+"<tr><td colspan=\"2\">"+Main.server.getProtocol().get("DESCRIPRION")+"</td></tr>"
							+"</table></html>"
						);
				}
			});
			
		
		
		
		// Окно авторизации
		createWindow(WINDOW_LOGIN)
			//.addLayout()
			//.addComponent("ServersLabel", new LabelC(Main.locale.get("label_server_choose")), LoginTheme.NODE_SERVER_LABEL)
			.addComponent("ServersList", new ComboBoxC<ServerVO>(new ServerChangeAction(),"server").genGetter(FormLoginAction.call()), LoginTheme.NODE_SERVER)
			//.addComponent("EmailLabel", new LabelC(Main.locale.get("label_login")), LoginTheme.NODE_LOGIN_LABEL)
			.addComponent("EmailField", new FieldC(20,"login","Login").genGetter(FormLoginAction.call()), LoginTheme.NODE_LOGIN)
			//.addComponent("PasswordLabel", new LabelC(Main.locale.get("label_password")), LoginTheme.NODE_PASSWORD_LABEL)
			.addComponent("PasswordField", new PassFieldC(20,"password").genGetter(FormLoginAction.call()), LoginTheme.NODE_PASSWORD)
			.addComponent("SubmitButton", new ButtonC(Main.locale.get("btn_auth_submit"),FormLoginAction.call()), LoginTheme.NODE_BUTTON_SUBMIT)
			.addComponent("RegisterButton", new ButtonC(Main.locale.get("btn_reg_redirect"), new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						changeWindow(Frame.WINDOW_REGISTER);
					}
				}), LoginTheme.NODE_BUTTON_REGISTER)
			.addComponent("SettingsButton", new ButtonC(Main.locale.get("btn_settings"),new SettingsWinAction()), LoginTheme.NODE_BUTTON_SETTINGS)
			.addComponent("PasswordForgot", new ButtonC(Main.locale.get("btn_forgot_password"), new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							Desktop.getDesktop().browse(new URI(Main.server.getProtocol().getLinkForgotPassword()));
						} catch (Exception ex) {Log.print("Некорректная ссылка для редиректа на восстановление пароля.");}
					}
				}), LoginTheme.NODE_BUTTON_PASSWORD_FORGOT)
			.addComponent("InfoLabel", new LabelC(),MainTheme.NODE_INFO_LABEL)
			.addComponent("Banner", new ImageC(), MainTheme.NODE_BANNER)
			.addComponent("BannerBlock", new ImageC(MainTheme.BLOCK_FILE), MainTheme.NODE_BANNER_BLOCK)
			.addComponent("BlockInfo", new ImageC(MainTheme.BLOCK_FILE), MainTheme.NODE_BLOCK_INFO)
			.setUpdateFunc(() -> {
				/*((ImageC) WINDOWS.get(WINDOW_LOGIN).getComponent("Banner")).setURL(Main.server.getProtocol().get("BANNER"));
				Window instance = WINDOWS.get(WINDOW_LOGIN);
				if (instance.getComponent("InfoLabel") instanceof LabelC) {
					LabelC InfoLabel = (LabelC) instance.getComponent("InfoLabel");
					InfoLabel.setText("<html><table>"
							+"<tr><td>"+Main.locale.get("server_name")+"</td><td>"+Main.server.getProtocol().get("SERVER_NAME")+"</td></tr>"
							+"<tr><td>"+Main.locale.get("server_online")+"</td><td>"+Main.server.getProtocol().get("PLAYERS_ONLINE")+"/"+Main.server.getProtocol().get("PLAYERS_MAX")+"</td></tr>"
							+"<tr><td>Discord:    </td><td><a href='"+Main.server.getProtocol().get("DISCORD")+"'>"+Main.server.getProtocol().get("DISCORD")+"</a></td></tr>"
							+"</table></html>"
						);
				}*/
				updateServerInfo();
			});
		
		
		
		// Окно регистрации
		createWindow(WINDOW_REGISTER)
			//.addLayout()
			//.addComponent("ServerLabel", new LabelC(Main.locale.get("label_server")), RegisterTheme.NODE_SERVER_LABEL)
			.addComponent("ServerField", new LabelC(), RegisterTheme.NODE_SERVER)
			.addComponent("ImageBlock", new ImageC(MainTheme.BLOCK_FILE),new ThemeItemNode(550,50,245,30))
			//.addComponent("LoginLabel", new LabelC(Main.locale.get("label_login")), RegisterTheme.NODE_LOGIN_LABEL)
			.addComponent("LoginField", new FieldC(20,"login","Login").genGetter(FormRegAction.call()), LoginTheme.NODE_LOGIN)
			//.addComponent("PasswordLabel", new LabelC(Main.locale.get("label_password")), RegisterTheme.NODE_PASSWORD_LABEL)
			.addComponent("PasswordField", new PassFieldC(20,"password").genGetter(FormRegAction.call()), LoginTheme.NODE_PASSWORD)
			.addComponent("SubmitButton", new ButtonC(Main.locale.get("btn_reg_submit"),FormRegAction.call()), LoginTheme.NODE_BUTTON_SUBMIT)
			.addComponent("LoginButton", new ButtonC(Main.locale.get("btn_auth_redirect"), new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					changeWindow(Frame.WINDOW_LOGIN);
				}
			}), LoginTheme.NODE_BUTTON_REGISTER)
			.addComponent("InfoLabel", new LabelC(),MainTheme.NODE_INFO_LABEL)
			.addComponent("Banner", new ImageC(), MainTheme.NODE_BANNER)
			.addComponent("BannerBlock", new ImageC(MainTheme.BLOCK_FILE), new ThemeItemNode(5, 30, 532, 232))
			.addComponent("BlockInfo", new ImageC(MainTheme.BLOCK_FILE), MainTheme.NODE_BLOCK_INFO)
			.setUpdateFunc(() -> {
				Window instance = WINDOWS.get(WINDOW_REGISTER);
				if (instance.getComponent("ServerField") instanceof LabelC) {
					((LabelC) instance.getComponent("ServerField")).setText(Main.server.getProtocol().get("SERVER_NAME"));
				}
				
				((ImageC) WINDOWS.get(WINDOW_REGISTER).getComponent("Banner")).setURL(Main.server.getProtocol().get("BANNER"));
				Window instanc = WINDOWS.get(WINDOW_REGISTER);
				if (instanc.getComponent("InfoLabel") instanceof LabelC) {
					LabelC InfoLabel = (LabelC) instanc.getComponent("InfoLabel");
					InfoLabel.setText("<html><table>"
							+"<tr><td>"+Main.locale.get("server_name")+"</td><td>"+Main.server.getProtocol().get("SERVER_NAME")+"</td></tr>"
							+"<tr><td>"+Main.locale.get("server_online")+"</td><td>"+Main.server.getProtocol().get("PLAYERS_ONLINE")+"/"+Main.server.getProtocol().get("PLAYERS_MAX")+"</td></tr>"
							+"<tr><td>Discord:    </td><td>"+Main.server.getProtocol().get("DISCORD")+"</td></tr>"
							+"<tr><td>Веб-сайт:</td><td>"+Main.server.getProtocol().get("WEB_SITE")+"</td></tr>"
							+"<tr></tr>"
							+"<tr><td colspan=\"2\">"+Main.server.getProtocol().get("DESCRIPRION")+"</td></tr>"
							+"</table></html>"
						);
				}
			});
		
		
		
		// Окно настроек
		createWindow(WINDOW_SETTINGS)
			//.addLayout()
			.addComponent("LanguageLabel", new LabelC(Main.locale.get("launcher_language")), SettingsTheme.NODE_LANGUAGE_LABEL)
			.addComponent("LanguageList", new ComboBoxC<Locale>(new ChangeLocaleAction(),"LanguageList"), SettingsTheme.NODE_LANGUAGE)
			.addComponent("BackButton", new ButtonC(Main.locale.get("btn_back"), new BackWinAction()), SettingsTheme.NODE_BUTTON_BACK)
			.addComponent("LaunchFileLabel", new LabelC(""), SettingsTheme.NODE_FILE_CHOOSE_LABEL)
			.addComponent("LaunchFile", new ButtonC(Main.locale.get("btn_change_file_game"),new ChangeClientAction()), SettingsTheme.NODE_FILE_CHOOSE)
			/*.addComponent("DenyBackgoundWorkLabel", new LabelC(Main.locale.get("msg_background_work_deny")), SettingsTheme.NODE_DENY_LABEL)
			.addComponent("DenyBackgroundWork", new CheckBoxC(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (((CheckBoxC) WINDOWS.get(WINDOW_SETTINGS).getComponent("DenyBackgroundWork")).isSelected()) {
						Config.BACKGROUND_WORCK_DENY = true;
					} else {
						Config.BACKGROUND_WORCK_DENY = false;
					}
					Config.save();
				}
			}), SettingsTheme.NODE_DENY)*/
			.addComponent("ClientSettingsButton", new ButtonC(Main.locale.get("btn_client_settings"),new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					changeWindow(Frame.WINDOW_CLIENT_SETTINGS);
				}
			}), SettingsTheme.NODE_BUTTON_SETTINGS)
			.addComponent("Block", new ImageC(MainTheme.BLOCK_FILE), new ThemeItemNode(5, 30, 532, 270))
			.addComponent("AboutButton", new ButtonC("О лаунчере",new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					changeWindow(Frame.WINDOW_ABOUT);
				}
			}), SettingsTheme.NODE_BUTTON_ABOUT)
			.setUpdateFunc(() -> {
				((LabelC) WINDOWS.get(WINDOW_SETTINGS).getComponent("LaunchFileLabel")).setText(Config.GAME_PATH);
			});
		//((CheckBoxC) WINDOWS.get(WINDOW_SETTINGS).getComponent("DenyBackgroundWork")).setSelected(Config.BACKGROUND_WORCK_DENY);
		
		
		
		// Окно настроек клиента
		createWindow(WINDOW_CLIENT_SETTINGS)
			//.addLayout()
			.addComponent("LabelLangCli", new LabelC(Main.locale.get("msg_client_language")), ClientSettingsTheme.NODE_LABEL_LANG)
			.addComponent("langCli", new ComboBoxC<ComboboxString<String>>(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String s = ((ComboboxString<String>) ((ComboBoxC<ComboboxString<String>>) WINDOWS.get(WINDOW_CLIENT_SETTINGS).getComponent("langCli")).getSelectedItem()).getValue();
					if (s != null)
						ClientConfigAction.setLanguage(s);
				}
			},"langCheckbox"), ClientSettingsTheme.NODE_COMBOBOX_LANG)
			.addComponent("labelAudiomode", new LabelC(Main.locale.get("msg_client_audiomode")), ClientSettingsTheme.NODE_LABEL_AUDIOMODE)
			.addComponent("Audiomode", new ComboBoxC<ComboboxString<Boolean>>(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Boolean s = ((ComboboxString<Boolean>) ((ComboBoxC<ComboboxString<Boolean>>) WINDOWS.get(WINDOW_CLIENT_SETTINGS).getComponent("Audiomode")).getSelectedItem()).getValue();
					if (s != null)
						ClientConfigAction.setAudiomode(s);
				}
			}, "Audiomode"), ClientSettingsTheme.NODE_CHECKBOX_AUDIOMODE)
			.addComponent("labelPerformance", new LabelC(Main.locale.get("msg_client_performance")), ClientSettingsTheme.NODE_LABEL_PERFORMANCE)
			.addComponent("performance", new ComboBoxC<ComboboxString<Integer>>(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Integer s = ((ComboboxString<Integer>) ((ComboBoxC<ComboboxString<Integer>>) WINDOWS.get(WINDOW_CLIENT_SETTINGS).getComponent("performance")).getSelectedItem()).getValue();
					if (s != null)
						ClientConfigAction.setPerformancelevel(s);
				}
			}, "performance"), ClientSettingsTheme.NODE_COMBOBOX_PERFORMANCE)
			.addComponent("labelVsync", new LabelC(Main.locale.get("msg_client_vsync")), ClientSettingsTheme.NODE_LABEL_VSYNC)
			.addComponent("vsinc", new CheckBoxC(ClientConfigAction.getVsync(),new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (((CheckBoxC) WINDOWS.get(WINDOW_CLIENT_SETTINGS).getComponent("vsinc")).isSelected())
						ClientConfigAction.setVsync(true);
					else
						ClientConfigAction.setVsync(false);
				}
			}), ClientSettingsTheme.NODE_CHECKBOX_VSYNC)
			/*.addComponent("labelMotionBlue", new LabelC(Main.locale.get("msg_client_motionblur")), ClientSettingsTheme.NODE_LABEL_MOTIONBLURE)
			.addComponent("MotionBlue", new CheckBoxC(ClientConfigAction.getMotionblurenable(),new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (((CheckBoxC) WINDOWS.get(WINDOW_CLIENT_SETTINGS).getComponent("MotionBlue")).isSelected())
						ClientConfigAction.setMotionblurenable(true);
					else
						ClientConfigAction.setMotionblurenable(false);
				}
			}), ClientSettingsTheme.NODE_CHECKBOX_MOTIONBLURE)
			.addComponent("labeloverbright", new LabelC(Main.locale.get("msg_client_overbright")), ClientSettingsTheme.NODE_LABEL_OVERBRIGHT)
			.addComponent("overbright", new CheckBoxC(ClientConfigAction.getOverbrightenable(),new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (((CheckBoxC) WINDOWS.get(WINDOW_CLIENT_SETTINGS).getComponent("overbright")).isSelected())
						ClientConfigAction.setOverbrightenable(true);
					else
						ClientConfigAction.setOverbrightenable(false);
				}
			}), ClientSettingsTheme.NODE_CHECKBOX_OVERBRIGHT)
			.addComponent("labelparticlesystem", new LabelC(Main.locale.get("msg_client_particlesystem")), ClientSettingsTheme.NODE_LABEL_PARTICKE)
			.addComponent("particlesystem", new CheckBoxC(ClientConfigAction.getparticlesystemenable(),new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (((CheckBoxC) WINDOWS.get(WINDOW_CLIENT_SETTINGS).getComponent("particlesystem")).isSelected())
						ClientConfigAction.setParticlesystemenable(true);
					else
						ClientConfigAction.setParticlesystemenable(false);
				}
			}), ClientSettingsTheme.NODE_CHECKBOX_PARTICKE)
			.addComponent("labelWatersimenable", new LabelC(Main.locale.get("msg_client_watersim")), ClientSettingsTheme.NODE_LABEL_WATER)
			.addComponent("Watersimenable", new CheckBoxC(ClientConfigAction.getWatersimenable(),new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (((CheckBoxC) WINDOWS.get(WINDOW_CLIENT_SETTINGS).getComponent("Watersimenable")).isSelected())
						ClientConfigAction.setWatersimenable(true);
					else
						ClientConfigAction.setWatersimenable(false);
				}
			}), ClientSettingsTheme.NODE_CHECKBOX_WATER)*/
			.addComponent("labelAudoQuality", new LabelC(Main.locale.get("msg_client_audioquality")), ClientSettingsTheme.NODE_LABEL_AUDIOQUALITY)
			.addComponent("checkboxAudioQuality", new CheckBoxC(ClientConfigAction.getAudioquality(),new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (((CheckBoxC) WINDOWS.get(WINDOW_CLIENT_SETTINGS).getComponent("checkboxAudioQuality")).isSelected())
						ClientConfigAction.setAudioquality(true);
					else
						ClientConfigAction.setAudioquality(false);
				}
			}), ClientSettingsTheme.NODE_CHECKBOX_AUDIOQUALITY)
			.addComponent("labelBrightness", new LabelC(Main.locale.get("msg_client_brightness")), ClientSettingsTheme.NODE_LABEL_BRIGHTNESS)
			.addComponent("brightness", new SliderC(ClientConfigAction.getBrightness(),"brightness").genGetter(ClientConfigAction.call()), ClientSettingsTheme.NODE_SLIDER_BRIGHTNESS)
			.addComponent("Block", new ImageC(MainTheme.BLOCK_FILE), new ThemeItemNode(5, 30, 532, 220))
			.addComponent("SaveButton", new ButtonC(Main.locale.get("btn_client_settings_save"), ClientConfigAction.call()), ClientSettingsTheme.NODE_BUTTON_SAVE)
			.addComponent("BackButton", new ButtonC(Main.locale.get("btn_back"), new BackWinAction()), ClientSettingsTheme.NODE_BUTTON_BACK);
		
		ComboBoxC<ComboboxString<String>> c = null;
		if (getWindow(WINDOW_CLIENT_SETTINGS).getComponent("langCli") instanceof ComboBoxC<?>)
			c = (ComboBoxC<ComboboxString<String>>) WINDOWS.get(WINDOW_CLIENT_SETTINGS).getComponent("langCli");
		if (c != null) {
			c.removeAllItems();
			switch (ClientConfigAction.getLanguage()) {
			case "RU":
				c.addItem(new ComboboxString<String>("Русский", "RU"));
				break;
			case "EN":
				c.addItem(new ComboboxString<String>("English", "EN"));
				break;
			}
			
			if (!ClientConfigAction.getLanguage().equalsIgnoreCase("RU"))
				c.addItem(new ComboboxString<String>("Русский", "RU"));
			if (!ClientConfigAction.getLanguage().equalsIgnoreCase("EN"))
				c.addItem(new ComboboxString<String>("English", "EN"));
		}
		ComboBoxC<ComboboxString<Boolean>> cc = null;
		if (getWindow(WINDOW_CLIENT_SETTINGS).getComponent("Audiomode") instanceof ComboBoxC<?>)
			cc = (ComboBoxC<ComboboxString<Boolean>>) WINDOWS.get(WINDOW_CLIENT_SETTINGS).getComponent("Audiomode");
		
		if (cc != null) {
			cc.removeAllItems();
			
			if (ClientConfigAction.getAudiomode()) {
				cc.addItem(new ComboboxString<Boolean>(Main.locale.get("msg_client_audiomode_stereo"), true));
				cc.addItem(new ComboboxString<Boolean>(Main.locale.get("msg_client_audiomode_around"), false));
			}
			else {
				cc.addItem(new ComboboxString<Boolean>(Main.locale.get("msg_client_audiomode_around"), false));
				cc.addItem(new ComboboxString<Boolean>(Main.locale.get("msg_client_audiomode_stereo"), true));
			}
		}
		
		ComboBoxC<ComboboxString<Integer>> ccc = null;
		if (getWindow(WINDOW_CLIENT_SETTINGS).getComponent("performance") instanceof ComboBoxC<?>)
			ccc = (ComboBoxC<ComboboxString<Integer>>) WINDOWS.get(WINDOW_CLIENT_SETTINGS).getComponent("performance");
		
		if (ccc != null) {
			ccc.removeAllItems();
			
			switch (ClientConfigAction.getPerformancelevel()) {
			case 0:
				ccc.addItem(new ComboboxString<Integer>(Main.locale.get("msg_client_performance_min"), 0));
				break;
			case 1:
				ccc.addItem(new ComboboxString<Integer>(Main.locale.get("msg_client_performance_low"), 1));
				break;
			case 2:
				ccc.addItem(new ComboboxString<Integer>(Main.locale.get("msg_client_performance_mid"), 2));
				break;
			case 3:
				ccc.addItem(new ComboboxString<Integer>(Main.locale.get("msg_client_performance_high"), 3));
				break;
			case 4:
				ccc.addItem(new ComboboxString<Integer>(Main.locale.get("msg_client_performance_max"), 4));
				break;
			case 5:
				ccc.addItem(new ComboboxString<Integer>(Main.locale.get("msg_client_performance_other"), 5));
				break;
			}
			
			if (ClientConfigAction.getPerformancelevel() != 0)
				ccc.addItem(new ComboboxString<Integer>(Main.locale.get("msg_client_performance_min"), 0));
			
			if (ClientConfigAction.getPerformancelevel() != 1)
				ccc.addItem(new ComboboxString<Integer>(Main.locale.get("msg_client_performance_low"), 1));
			
			if (ClientConfigAction.getPerformancelevel() != 2)
				ccc.addItem(new ComboboxString<Integer>(Main.locale.get("msg_client_performance_mid"), 2));
			
			if (ClientConfigAction.getPerformancelevel() != 3)
				ccc.addItem(new ComboboxString<Integer>(Main.locale.get("msg_client_performance_high"), 3));
			
			if (ClientConfigAction.getPerformancelevel() != 4)
				ccc.addItem(new ComboboxString<Integer>(Main.locale.get("msg_client_performance_max"), 4));
			
			if (ClientConfigAction.getPerformancelevel() != 5)
				ccc.addItem(new ComboboxString<Integer>(Main.locale.get("msg_client_performance_other"), 5));
		}
		
		
		// Окно О лаунчере
		createWindow(WINDOW_ABOUT)
			//.addLayout()
			.addComponent("abotText", new LabelC(
					"<html><body style=\"width: 400px\">"
					+ "<center>О лаунчере</center>"
					+ "Версия лаунчера: "+Config.VERSION+"<br>"
					+ "Версия дизайна: 0.0.2 (preview)<br>"
					+ "Разработчик лаунчера: Vadimka - vadik.golubeff@yandex.ru<br>"
					+ "Автор дизайна: Ryan Cooper - Discord: Ryan Cooper#9057<br>"
					+ "Автор дизайна: Neville Cooper - youtube.com/channel/UCqZzFjtApV6dR1heNWpTuOQ<br>"
					+ "Список нововведений в этой версии:<br>"
					+ "<ul>"
					+ "<li>Разработан дизайн</li>"
					+ "<li>Добавлена проверка целостности клиента</li>"
					+ "<li>Добавлена интеграция Discord</li>"
					+ "</ul>"
					+ "</body></html>"), SettingsTheme.NODE_TEXT_ABOUT)
			.addComponent("BannerBlock", new ImageC(MainTheme.BLOCK_FILE), new ThemeItemNode(100, 60, 600, 300))
			.addComponent("btnBack", new ButtonC(Main.locale.get("btn_back"), new BackWinAction()), SettingsTheme.NODE_BUTTON_BACK);
		
		addComponent(new LabelC(Config.WINDOW_TITLE+"     v: "+Config.VERSION,MainTheme.STYLE_TITLE_WINDOW), 10, 5, 300, 15);
		addComponent(new ImageC(MainTheme.STATUSBAR_FILE), 0,0,getWidth(),23);
		addComponent(new ImageC(MainTheme.DOWNBAR_FILE), 0,getHeight()-90,getWidth(),101);
		addComponent(new DynBgC(MainTheme.WORLDMAP_FILE), 0,23,getWidth(),getHeight()-90);
		addComponent(new ImageC(MainTheme.BACKGROUND_FILE), 0,0,getWidth(),getHeight());
		
		//setContentPane(mainPanel);
		
		VersionField.setText(Config.VERSION);
		CURRENT_WINDOW = WINDOW_LOADING;
		changeWindow(WINDOW_LOADING);
	}
	/**
	 * Обновить информацию о сервере на экране
	 */
	public void updateServerInfo() {
		((ImageC) WINDOWS.get(WINDOW_LOGIN).getComponent("Banner")).setURL(Main.server.getProtocol().get("BANNER"));
		Window instance = WINDOWS.get(WINDOW_LOGIN);
		if (instance.getComponent("InfoLabel") instanceof LabelC) {
			LabelC InfoLabel = (LabelC) instance.getComponent("InfoLabel");
			InfoLabel.setText("<html><table>"
					+"<tr><td>"+Main.locale.get("server_name")+"</td><td>"+Main.server.getProtocol().get("SERVER_NAME")+"</td></tr>"
					+"<tr><td>"+Main.locale.get("server_online")+"</td><td>"+Main.server.getProtocol().get("PLAYERS_ONLINE")+"/"+Main.server.getProtocol().get("PLAYERS_MAX")+"</td></tr>"
					+"<tr><td>Discord:    </td><td>"+Main.server.getProtocol().get("DISCORD")+"</td></tr>"
					+"<tr><td>Веб-сайт:</td><td>"+Main.server.getProtocol().get("WEB_SITE")+"</td></tr>"
					+"<tr></tr>"
					+"<tr><td colspan=\"2\">"+Main.server.getProtocol().get("DESCRIPRION")+"</td></tr>"
					+"</table></html>"
				);
		}
	}
	/**
	 * Получить окно
	 * @param id - идентификатор окна
	 * @return Window
	 */
	public Window getWindow(int id) {
		return WINDOWS.get(id);
	}
	/**
	 * Создать новое окно
	 * @param id - идентификатор окна
	 * @return объект созданного окна
	 */
	private Window createWindow(int id) {
		WINDOWS.put(id, new Window());
		WINDOWS.get(id).setVisible(false);
		addComponent(WINDOWS.get(id).update(), 0, 0, getWidth(), getHeight());
		return WINDOWS.get(id);
	}
	/**
	 * Добавить компонент на экран
	 * @param component - Добавляемый компонент
	 * @param x - расположение компонента по вертикали
	 * @param y - Расположение компонента по горизонтали
	 * @param Gwidth - Ширина объекта
	 * @param Gheight - Высота объекта
	 */
	private void addComponent(JComponent component, int x, int y, int Gwidth, int Gheight) {
		component.setBounds(x,y,Gwidth,Gheight);
		getContentPane().add(component);
	}
	/**
	 * Изменить текущее окно
	 * @param window - идентификатор окна
	 */
	public void changeWindow(int window) {
		WINDOWS.get(CURRENT_WINDOW).update().setVisible(false);
		CURRENT_WINDOW = window;
		if (WINDOWS.containsKey(window))
			WINDOWS.get(window).update().setVisible(true);
		getContentPane().validate();
		getContentPane().repaint();
	}
	/**
	 * Показать всплывающий диалог
	 * @param message - Сообщение
	 * @param title - Заголовок
	 */
	public void showDialog(String message, String title) {
		JOptionPane.showMessageDialog(this, message, title, javax.swing.JOptionPane.ERROR_MESSAGE, null);
	}
	/**
	 * Показывать диалог с вопросом
	 * @param title - Заголовок
	 * @param message - Сообщение
	 * @param options - Варианты выбора
	 * @return Выбранный вариант
	 */
	public Integer showQuestionDialog(String title, String message, Object[] options) {
		return JOptionPane.showOptionDialog(this, message,
				title, JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
	}
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {
		clickPoint = e.getPoint();
	}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseDragged(MouseEvent e) {
		if (
				MainTheme.SCROLL_ZONE_ENABLED &&
				MainTheme.SCROLL_ZONE.getX() <= clickPoint.x &&
				MainTheme.SCROLL_ZONE.getY() <= clickPoint.y &&
				MainTheme.SCROLL_ZONE.getX()+MainTheme.SCROLL_ZONE.getWidth() >= clickPoint.x &&
				MainTheme.SCROLL_ZONE.getY()+MainTheme.SCROLL_ZONE.getHeight() >= clickPoint.y
				) {
			int thisX = getLocation().x;
			int thisY = getLocation().y;
			
			int xMoved = (thisX + e.getX()) - (thisX + clickPoint.x);
			int yMoved = (thisY + e.getY()) - (thisY + clickPoint.y);
			
			setLocation(thisX + xMoved, thisY + yMoved);
		}
	}
	@Override
	public void mouseMoved(MouseEvent e) {}
	@Override
	public void windowActivated(WindowEvent arg0) {}
	@Override
	public void windowClosed(WindowEvent arg0) {}
	@Override
	public void windowClosing(WindowEvent arg0) {
		Main.shutdown(0);
	}
	@Override
	public void windowDeactivated(WindowEvent arg0) {}
	@Override
	public void windowDeiconified(WindowEvent arg0) {}
	@Override
	public void windowIconified(WindowEvent arg0) {}
	@Override
	public void windowOpened(WindowEvent arg0) {}
}
