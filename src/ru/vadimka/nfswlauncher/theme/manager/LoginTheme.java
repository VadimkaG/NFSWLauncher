package ru.vadimka.nfswlauncher.theme.manager;

import java.io.File;

public abstract class LoginTheme {
	public static boolean LAYOUT_USE = true;
	public static boolean BACKGROUND_USE = false;
	public static File BACKGROUND_FILE;
	
	public static ThemeItemNode NODE_LAYOUT = null;
	
	public static ThemeItemNode NODE_BANNER = null;

	public static ThemeItemNode NODE_SERVER_LABEL = new ThemeItemNode(0,0,1,1);
	public static ThemeItemNode NODE_SERVER = new ThemeItemNode(550,50,245,30);
	public static ThemeItemNode NODE_LOGIN_LABEL = new ThemeItemNode(0,1,1,1);
	public static ThemeItemNode NODE_LOGIN = new ThemeItemNode(360,425,200,40);
	public static ThemeItemNode NODE_PASSWORD_LABEL = new ThemeItemNode(0,2,1,1);
	public static ThemeItemNode NODE_PASSWORD = new ThemeItemNode(360,465,200,40);
	
	public static ThemeItemNode NODE_BUTTON_SUBMIT = new ThemeItemNode(587,425,200,40);
	public static ThemeItemNode NODE_BUTTON_REGISTER = new ThemeItemNode(587,465,200,40);
	public static ThemeItemNode NODE_BUTTON_SETTINGS = new ThemeItemNode(737,1,20,20);
	public static ThemeItemNode NODE_BUTTON_PASSWORD_FORGOT = new ThemeItemNode(550,100,200,40);
	
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
			case "background":
				File file = new File(ThemeManager.themesDir.getAbsolutePath()+File.separator+Config.CURRENT_THEME+File.separator+item.getTextContent());
				if (file.exists() && !file.isDirectory()) {
					BACKGROUND_FILE = file;
					BACKGROUND_USE = true;
				} else {
					Log.print("Фон окна логина не найден. Не найденый путь: "+file.getAbsolutePath());
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
					case "server_label":
						NODE_SERVER.setLabel(ThemeManager.getItem(parentNode.getChildNodes()));
						break;
					case "server":
						NODE_SERVER.setField(ThemeManager.getItem(parentNode.getChildNodes()));
						break;
					case "login_label":
						NODE_LOGIN.setLabel(ThemeManager.getItem(parentNode.getChildNodes()));
						break;
					case "login":
						NODE_LOGIN.setField(ThemeManager.getItem(parentNode.getChildNodes()));
						break;
					case "password_label":
						NODE_PASSWORD.setLabel(ThemeManager.getItem(parentNode.getChildNodes()));
						break;
					case "password":
						NODE_PASSWORD.setField(ThemeManager.getItem(parentNode.getChildNodes()));
						break;
					case "language_label":
						NODE_LANGUAGE.setLabel(ThemeManager.getItem(parentNode.getChildNodes()));
						break;
					case "language":
						NODE_LANGUAGE.setField(ThemeManager.getItem(parentNode.getChildNodes()));
						break;
					case "submit":
						NODE_BUTTON_SUBMIT.setField(ThemeManager.getItem(parentNode.getChildNodes()));
						break;
					case "register":
						NODE_BUTTON_REGISTER.setField(ThemeManager.getItem(parentNode.getChildNodes()));
						break;
					case "settings":
						NODE_BUTTON_SETTINGS.setField(ThemeManager.getItem(parentNode.getChildNodes()));
						break;
					case "password_restore":
						NODE_BUTTON_PASSWORD_FORGOT.setField(ThemeManager.getItem(parentNode.getChildNodes()));
						break;
					}
				}
				break;
			}
		}
	}*/
}
