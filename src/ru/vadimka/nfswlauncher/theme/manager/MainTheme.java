package ru.vadimka.nfswlauncher.theme.manager;

import java.awt.Color;
import java.awt.Image;

import javax.imageio.ImageIO;

import ru.vadimka.nfswlauncher.Log;
import ru.vadimka.nfswlauncher.theme.Frame;

public abstract class MainTheme {
	public static boolean LAYOUT_USE = true;
	public static Image BACKGROUND_FILE = null;
	public static Image WORLDMAP_FILE = null;
	public static Image STATUSBAR_FILE = null;
	public static Image DOWNBAR_FILE = null;
	public static Image BLOCK_FILE = null;
	
	public static StyleItem STYLE_TITLE_WINDOW;
	
	public static ThemeItemNode NODE_LAYOUT = null;
	
	/* layout values
	public static ThemeItemNode NODE_INFO_LABEL = new ThemeItemNode(0,0,2,1);
	public static ThemeItemNode NODE_LOGIN = new ThemeItemNode(1,0,1,1);
	public static ThemeItemNode NODE_SERVER = new ThemeItemNode( 1,1,1,1);
	public static ThemeItemNode NODE_SERVER_ONLINE = new ThemeItemNode(1,2,1,1);
	public static ThemeItemNode NODE_SERVER_REGISTRED = new ThemeItemNode(1,3,1,1);
	public static ThemeItemNode NODE_SERVER_DISCORD = new ThemeItemNode(1,4,1,1);
	public static ThemeItemNode NODE_GAME_FILE = new ThemeItemNode(1,5,1,1);
	public static ThemeItemNode NODE_BUTTON_START = new ThemeItemNode(0,6,1,1);
	public static ThemeItemNode NODE_BUTTON_CHANGE = new ThemeItemNode(1,6,1,1);
	public static ThemeItemNode NODE_BUTTON_SETTINGS = new ThemeItemNode(0,7,2,1);
	*/
	
	public static boolean SCROLL_ZONE_ENABLED = true;
	public static ThemeItemNode SCROLL_ZONE = new ThemeItemNode(0,0,800,21);
	
	public static ThemeItemNode NODE_TEXT_LOADING = new ThemeItemNode(300,200,300,30);
	
	public static ThemeItemNode NODE_INFO_LABEL = new ThemeItemNode(15, 265, 445, 150);
	public static ThemeItemNode NODE_BUTTON_START = new ThemeItemNode(587,425,200,40);
	public static ThemeItemNode NODE_BUTTON_CHANGE = new ThemeItemNode(587,465,200,40);
	public static ThemeItemNode NODE_BUTTON_SETTINGS = new ThemeItemNode(737,1,20,20);
	
	public static ThemeItemNode NODE_BUTTON_CLOSE = new ThemeItemNode(0,0,0,0);
	public static ThemeItemNode NODE_BUTTON_HIDE = new ThemeItemNode(0,0,0,0);
	
	public static ThemeItemNode NODE_BLOCK_INFO = new ThemeItemNode(10, 265, 450, 150);
	public static ThemeItemNode NODE_BANNER = new ThemeItemNode(10, 35, 523, 223);
	public static ThemeItemNode NODE_BANNER_BLOCK = new ThemeItemNode(5, 30, 532, 232);
	
	public static void load() {
		try {
			BACKGROUND_FILE = ImageIO.read(Frame.class.getResourceAsStream("background.png"));
			WORLDMAP_FILE = ImageIO.read(Frame.class.getResourceAsStream("worldmap.png"));
			STATUSBAR_FILE = ImageIO.read(Frame.class.getResourceAsStream("status_bar.png"));
			DOWNBAR_FILE = ImageIO.read(Frame.class.getResourceAsStream("down_bar.png"));
			BLOCK_FILE = ImageIO.read(Frame.class.getResourceAsStream("block.png"));
			
			STYLE_TITLE_WINDOW = new StyleItem();
			
			STYLE_TITLE_WINDOW.setTextColor(Color.WHITE);
			
			NODE_INFO_LABEL.getStyle().setTextColor(Color.CYAN);
			
			NODE_TEXT_LOADING.getStyle().setTextColor(Color.WHITE);
			
			NODE_BUTTON_CLOSE.getStyle().setBackground(
					ImageIO.read(Frame.class.getResourceAsStream("btn_exit.png")), 
					ImageIO.read(Frame.class.getResourceAsStream("btn_exit_pressed.png")), 
					ImageIO.read(Frame.class.getResourceAsStream("btn_exit_focus.png")));
			
			NODE_BUTTON_HIDE.getStyle().setBackground(
					ImageIO.read(Frame.class.getResourceAsStream("btn_hide.png")), 
					ImageIO.read(Frame.class.getResourceAsStream("btn_hide_pressed.png")),
					ImageIO.read(Frame.class.getResourceAsStream("btn_hide_focus.png")));
			
			Image btn_settings = ImageIO.read(Frame.class.getResourceAsStream("btn_settings.png"));
			Image btn_settings_pressed = ImageIO.read(Frame.class.getResourceAsStream("btn_settings_pressed.png"));
			Image btn_settings_focus = ImageIO.read(Frame.class.getResourceAsStream("btn_settings_focus.png"));
			
			NODE_BUTTON_SETTINGS.getStyle().setBackground(btn_settings, btn_settings_pressed,btn_settings_focus);
			LoginTheme.NODE_BUTTON_SETTINGS.getStyle().setBackground(btn_settings, btn_settings_pressed,btn_settings_focus);
			LoginTheme.NODE_BUTTON_SETTINGS.getStyle().textHide(true);
			
			Image btn = ImageIO.read(Frame.class.getResourceAsStream("btn_start.png"));
			Image btn_pressed = ImageIO.read(Frame.class.getResourceAsStream("btn_start_pressed.png"));
			Image btn_focus = ImageIO.read(Frame.class.getResourceAsStream("btn_start_focus.png"));
			
			LoginTheme.NODE_BUTTON_SUBMIT.getStyle().setBackground(btn, btn_pressed, btn_focus);
			LoginTheme.NODE_BUTTON_SUBMIT.getStyle().setTextColor(Color.WHITE);
			LoginTheme.NODE_BUTTON_REGISTER.getStyle().setBackground(btn, btn_pressed, btn_focus);
			LoginTheme.NODE_BUTTON_REGISTER.getStyle().setTextColor(Color.WHITE);
			LoginTheme.NODE_BUTTON_PASSWORD_FORGOT.getStyle().setBackground(btn, btn_pressed, btn_focus);
			LoginTheme.NODE_BUTTON_PASSWORD_FORGOT.getStyle().setTextColor(Color.WHITE);
			
			NODE_BUTTON_START.getStyle().setBackground(btn, btn_pressed, btn_focus);
			NODE_BUTTON_START.getStyle().setTextColor(Color.WHITE);
			NODE_BUTTON_CHANGE.getStyle().setBackground(btn, btn_pressed, btn_focus);
			NODE_BUTTON_CHANGE.getStyle().setTextColor(Color.WHITE);
			
			NODE_BUTTON_SETTINGS.getStyle().textHide(true);
			
			RegisterTheme.NODE_SERVER.getStyle().setTextColor(Color.WHITE);
			
			SettingsTheme.NODE_BUTTON_BACK.getStyle().setBackground(btn, btn_pressed, btn_focus);
			SettingsTheme.NODE_BUTTON_BACK.getStyle().setTextColor(Color.WHITE);
			
			SettingsTheme.NODE_BUTTON_ABOUT.getStyle().setBackground(btn, btn_pressed, btn_focus);
			SettingsTheme.NODE_BUTTON_ABOUT.getStyle().setTextColor(Color.WHITE);
			
			SettingsTheme.NODE_TEXT_ABOUT.getStyle().setTextColor(Color.WHITE);
			
			SettingsTheme.NODE_LANGUAGE_LABEL.getStyle().setTextColor(Color.WHITE);
			SettingsTheme.NODE_FILE_CHOOSE_LABEL.getStyle().setTextColor(Color.WHITE);
			
			ClientSettingsTheme.NODE_LABEL_AUDIOMODE.getStyle().setTextColor(Color.WHITE);
			ClientSettingsTheme.NODE_LABEL_AUDIOQUALITY.getStyle().setTextColor(Color.WHITE);
			ClientSettingsTheme.NODE_LABEL_BRIGHTNESS.getStyle().setTextColor(Color.WHITE);
			ClientSettingsTheme.NODE_LABEL_LANG.getStyle().setTextColor(Color.WHITE);
			ClientSettingsTheme.NODE_LABEL_PERFORMANCE.getStyle().setTextColor(Color.WHITE);
			ClientSettingsTheme.NODE_LABEL_VSYNC.getStyle().setTextColor(Color.WHITE);
			
			ClientSettingsTheme.NODE_BUTTON_BACK.getStyle().setBackground(btn, btn_pressed, btn_focus);
			ClientSettingsTheme.NODE_BUTTON_BACK.getStyle().setTextColor(Color.WHITE);
			
			ClientSettingsTheme.NODE_BUTTON_SAVE.getStyle().setBackground(btn, btn_pressed, btn_focus);
			ClientSettingsTheme.NODE_BUTTON_SAVE.getStyle().setTextColor(Color.WHITE);
			
		} catch (Exception e1) {
			Log.print("Не возможно загрузить иконку: "+e1.getMessage());
		}
	}
	
	/*public static void parceTheme(NodeList items) {
		String tmp_str = "";
		NodeList parentNodes = null;
		Node parentNode = null;
		for (int i = 0; i < items.getLength(); i++) {
			Node item = items.item(i);
			switch(item.getNodeName()) {
			case "layout":
				tmp_str = item.getTextContent();
				if (tmp_str.equalsIgnoreCase("true")) LAYOUT_USE = true;
				else LAYOUT_USE = false;
				break;
			case "grid":
				NODE_LAYOUT = ThemeManager.getItem(item.getChildNodes());
				break;
			case "banner":
				NODE_BANNER = ThemeManager.getItem(item.getChildNodes());
				break;
			case "background":
				File file = new File(ThemeManager.themesDir.getAbsolutePath()+File.separator+Config.CURRENT_THEME+File.separator+item.getTextContent());
				if (file.exists() && !file.isDirectory()) {
					BACKGROUND_FILE = file;
					BACKGROUND_USE = true;
				} else {
					Log.print("Фон главного окна не найден. Не найденый путь: "+file.getAbsolutePath());
				}
				break;
			case "items":
				parentNodes = item.getChildNodes();
				for (int ii = 0; ii < parentNodes.getLength(); ii++) {
					parentNode = parentNodes.item(ii);
					switch(parentNode.getNodeName()) {
					case "banner":
						NODE_BANNER = new ThemeItemNode(0,0,0,0);
						NODE_BANNER.setField(ThemeManager.getItem(parentNode.getChildNodes()));
						break;
					case "login_label":
						NODE_LOGIN.setLabel(ThemeManager.getItem(parentNode.getChildNodes()));
						break;
					case "login":
						NODE_LOGIN.setField(ThemeManager.getItem(parentNode.getChildNodes()));
						break;
					case "server_label":
						NODE_SERVER.setLabel(ThemeManager.getItem(parentNode.getChildNodes()));
						break;
					case "server":
						NODE_SERVER.setField(ThemeManager.getItem(parentNode.getChildNodes()));
						break;
					case "online_label":
						NODE_SERVER_ONLINE.setLabel(ThemeManager.getItem(parentNode.getChildNodes()));
						break;
					case "online":
						NODE_SERVER_ONLINE.setField(ThemeManager.getItem(parentNode.getChildNodes()));
						break;
					case "registred_label":
						NODE_SERVER_REGISTRED.setLabel(ThemeManager.getItem(parentNode.getChildNodes()));
						break;
					case "registred":
						NODE_SERVER_REGISTRED.setField(ThemeManager.getItem(parentNode.getChildNodes()));
						break;
					case "discord_label":
						NODE_SERVER_DISCORD.setLabel(ThemeManager.getItem(parentNode.getChildNodes()));
						break;
					case "discord":
						NODE_SERVER_DISCORD.setField(ThemeManager.getItem(parentNode.getChildNodes()));
						break;
					case "game_file_label":
						NODE_GAME_FILE.setLabel(ThemeManager.getItem(parentNode.getChildNodes()));
						break;
					case "game_file":
						NODE_GAME_FILE.setField(ThemeManager.getItem(parentNode.getChildNodes()));
						break;
					case "start":
						NODE_BUTTON_START.setField(ThemeManager.getItem(parentNode.getChildNodes()));
						break;
					case "change_user":
						NODE_BUTTON_CHANGE.setField(ThemeManager.getItem(parentNode.getChildNodes()));
						break;
					case "settings":
						NODE_BUTTON_SETTINGS.setField(ThemeManager.getItem(parentNode.getChildNodes()));
						break;
					}
				}
				break;
			}
		}
	}*/
}
