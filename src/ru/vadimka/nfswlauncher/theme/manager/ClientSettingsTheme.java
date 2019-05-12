package ru.vadimka.nfswlauncher.theme.manager;

import java.io.File;

public class ClientSettingsTheme {
	public static boolean LAYOUT_USE = true;
	public static boolean BACKGROUND_USE = false;
	public static File BACKGROUND_FILE;
	
	public static ThemeItemNode NODE_LAYOUT = null;
	
	public static ThemeItemNode NODE_BANNER = null;

	public static ThemeItemNode NODE_LABEL_LANG = new ThemeItemNode(50,40,250,30);
	public static ThemeItemNode NODE_COMBOBOX_LANG = new ThemeItemNode(260,40,250,30);
	public static ThemeItemNode NODE_CHECKBOX_AUDIOMODE = new ThemeItemNode(260,75,250,30);
	public static ThemeItemNode NODE_LABEL_AUDIOMODE = new ThemeItemNode(50,75,250,30);
	public static ThemeItemNode NODE_CHECKBOX_AUDIOQUALITY = new ThemeItemNode(260,115,20,20);
	public static ThemeItemNode NODE_LABEL_AUDIOQUALITY = new ThemeItemNode(50,110,250,30);
	public static ThemeItemNode NODE_SLIDER_BRIGHTNESS = new ThemeItemNode(260,140,250,30);
	public static ThemeItemNode NODE_LABEL_BRIGHTNESS = new ThemeItemNode(50,140,250,30);
	public static ThemeItemNode NODE_COMBOBOX_PERFORMANCE = new ThemeItemNode(260,175,250,30);
	public static ThemeItemNode NODE_LABEL_PERFORMANCE = new ThemeItemNode(50,175,250,30);
	public static ThemeItemNode NODE_CHECKBOX_VSYNC = new ThemeItemNode(260,215,20,20);
	public static ThemeItemNode NODE_LABEL_VSYNC = new ThemeItemNode(50,210,250,30);
	/*public static ThemeItemNode NODE_CHECKBOX_MOTIONBLURE = new ThemeItemNode(260,250,250,30);
	public static ThemeItemNode NODE_LABEL_MOTIONBLURE = new ThemeItemNode(50,250,250,30);
	public static ThemeItemNode NODE_CHECKBOX_OVERBRIGHT = new ThemeItemNode(260,250,250,30);
	public static ThemeItemNode NODE_LABEL_OVERBRIGHT = new ThemeItemNode(50,250,250,30);
	public static ThemeItemNode NODE_CHECKBOX_PARTICKE = new ThemeItemNode(260,280,250,30);
	public static ThemeItemNode NODE_LABEL_PARTICKE = new ThemeItemNode(50,280,250,30);
	public static ThemeItemNode NODE_CHECKBOX_WATER = new ThemeItemNode(260,320,250,30);
	public static ThemeItemNode NODE_LABEL_WATER = new ThemeItemNode(50,320,250,30);*/
	public static ThemeItemNode NODE_BUTTON_SAVE = new ThemeItemNode(587,425,200,40);
	public static ThemeItemNode NODE_BUTTON_BACK = new ThemeItemNode(587,465,200,40);

}
