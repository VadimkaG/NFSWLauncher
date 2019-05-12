package ru.vadimka.nfswlauncher.theme.manager;

import java.io.File;

public abstract class SettingsTheme {
	public static boolean LAYOUT_USE = true;
	public static boolean BACKGROUND_USE = false;
	public static File BACKGROUND_FILE;
	
	public static ThemeItemNode NODE_LAYOUT = null;
	
	public static ThemeItemNode NODE_BANNER = null;

	public static ThemeItemNode QB_THEME = new ThemeItemNode(1,0,1,1);
	public static ThemeItemNode NODE_BUTTON_BACK = new ThemeItemNode(587,465,200,40);
	public static ThemeItemNode NODE_BUTTON_SETTINGS = new ThemeItemNode(260,250,250,30);
	public static ThemeItemNode NODE_BUTTON_ABOUT = new ThemeItemNode(587,425,200,40);
	
	public static ThemeItemNode NODE_LANGUAGE_LABEL = new ThemeItemNode(50,50,250,30);
	public static ThemeItemNode NODE_LANGUAGE = new ThemeItemNode(260,50,250,30);
	
	public static ThemeItemNode NODE_FILE_CHOOSE_LABEL = new ThemeItemNode(50,150,250,30);
	public static ThemeItemNode NODE_FILE_CHOOSE = new ThemeItemNode(260,150,250,30);
	
	public static ThemeItemNode NODE_DENY = new ThemeItemNode(0,3,1,1);
	public static ThemeItemNode NODE_DENY_LABEL = new ThemeItemNode(1,3,1,1);
	
	public static ThemeItemNode NODE_TEXT_ABOUT = new ThemeItemNode(150, 80, 550, 250);
	
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
					Log.print("Фон окна настроек не найден. Не найденый путь: "+file.getAbsolutePath());
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
					case "theme_label":
						QB_THEME.setLabel(ThemeManager.getItem(parentNode.getChildNodes()));
						break;
					case "theme":
						QB_THEME.setField(ThemeManager.getItem(parentNode.getChildNodes()));
						break;
					case "back":
						NODE_BUTTON_BACK.setField(ThemeManager.getItem(parentNode.getChildNodes()));
						break;
					}
				}
				break;
			}
		}
	}*/
}
