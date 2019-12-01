package ru.vadimka.nfswlauncher.theme;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;

import ru.vadimka.nfswlauncher.Log;
import ru.vadimka.nfswlauncher.Main;
import ru.vadimka.nfswlauncher.theme.manager.StyleItem;
import ru.vadimka.nfswlauncher.utils.AsyncTasksUtils;

public abstract class GUIResourseLoader {
	
	private static final String RESOURCE_PATH = "resources/theme/";
	
	public static void loadIcon(Loader<Image> loader) {
		AsyncTasksUtils.addTask(() -> {
			loader.proc(Toolkit.getDefaultToolkit().getImage(Main.class.getResource(RESOURCE_PATH+"icon.png")));
		},"Загрузка иконки");
	}
	
	public static InputStream getGameSettingsFile() {
		return Main.class.getResourceAsStream("resources/"+"UserSettings.xml");
	}
	
	public static InputStream getBackgroundIS() {
		return Main.class.getResourceAsStream(RESOURCE_PATH+"background.png");
	}
	public static InputStream getMapIS() {
		return Main.class.getResourceAsStream(RESOURCE_PATH+"worldmap.png");
	}
	public static InputStream getUpBarIS() {
		return Main.class.getResourceAsStream(RESOURCE_PATH+"status_bar.png");
	}
	public static InputStream getBlockIS() {
		return Main.class.getResourceAsStream(RESOURCE_PATH+"block.png");
	}
	public static InputStream getLeftBarIS() {
		return Main.class.getResourceAsStream(RESOURCE_PATH+"bar_left.png");
	}
	public static InputStream getTabFirstIS() {
		return Main.class.getResourceAsStream(RESOURCE_PATH+"tab1.png");
	}
	public static StyleItem getStyleBtnExit() {
		StyleItem s = new StyleItem().setBackground(
				Main.class.getResourceAsStream(RESOURCE_PATH+"btn_exit.png"), 
				Main.class.getResourceAsStream(RESOURCE_PATH+"btn_exit_pressed.png"), 
				Main.class.getResourceAsStream(RESOURCE_PATH+"btn_exit_focus.png")
			);
		return s;
	}
	public static StyleItem getStyleBtnHide() {
		StyleItem s = new StyleItem().setBackground(
				Main.class.getResourceAsStream(RESOURCE_PATH+"btn_hide.png"), 
				Main.class.getResourceAsStream(RESOURCE_PATH+"btn_hide_pressed.png"), 
				Main.class.getResourceAsStream(RESOURCE_PATH+"btn_hide_focus.png")
			);
		return s;
	}
	public static StyleItem getStyleBtnDiscord() {
		StyleItem s = new StyleItem().setBackground(
				Main.class.getResourceAsStream(RESOURCE_PATH+"Discord_off.png"), 
				Main.class.getResourceAsStream(RESOURCE_PATH+"Discord_on.png"), 
				Main.class.getResourceAsStream(RESOURCE_PATH+"Discord_on.png"),
				Main.class.getResourceAsStream(RESOURCE_PATH+"Discord_off.png")
			)
			.textHide(true);
		return s;
	}
	public static StyleItem getStyleBtnSite() {
		StyleItem s = new StyleItem().setBackground(
				Main.class.getResourceAsStream(RESOURCE_PATH+"Site_off.png"), 
				Main.class.getResourceAsStream(RESOURCE_PATH+"Site_on.png"), 
				Main.class.getResourceAsStream(RESOURCE_PATH+"Site_on.png"),
				Main.class.getResourceAsStream(RESOURCE_PATH+"Site_off.png")
			)
			.textHide(true);
		return s;
	}
	public static StyleItem getStyleBtnServers() {
		StyleItem s = new StyleItem().setBackground(
				Main.class.getResourceAsStream(RESOURCE_PATH+"servers_on.png"), 
				Main.class.getResourceAsStream(RESOURCE_PATH+"servers_off.png"), 
				Main.class.getResourceAsStream(RESOURCE_PATH+"servers_mouse.png"),
				Main.class.getResourceAsStream(RESOURCE_PATH+"servers_off.png")
			)
			.textHide(true);
		return s;
	}
	public static StyleItem getStyleBtnSettings() {
		StyleItem s = new StyleItem().setBackground(
				Main.class.getResourceAsStream(RESOURCE_PATH+"setting_on.png"), 
				Main.class.getResourceAsStream(RESOURCE_PATH+"setting_off.png"), 
				Main.class.getResourceAsStream(RESOURCE_PATH+"setting_mouse.png"),
				Main.class.getResourceAsStream(RESOURCE_PATH+"setting_off.png")
			)
			.textHide(true);
		return s;
	}
	public static StyleItem getStyleBtnInfo() {
		StyleItem s = new StyleItem().setBackground(
				Main.class.getResourceAsStream(RESOURCE_PATH+"info_on.png"), 
				Main.class.getResourceAsStream(RESOURCE_PATH+"info_off.png"), 
				Main.class.getResourceAsStream(RESOURCE_PATH+"info_mouse.png"),
				Main.class.getResourceAsStream(RESOURCE_PATH+"info_off.png")
			)
			.textHide(true);
		return s;
	}
	public static StyleItem getStyleBtn() {
		StyleItem s = new StyleItem().setBackground(
				Main.class.getResourceAsStream(RESOURCE_PATH+"Button_1.png"), 
				Main.class.getResourceAsStream(RESOURCE_PATH+"Button_2.png"), 
				Main.class.getResourceAsStream(RESOURCE_PATH+"Button_3.png"),
				Main.class.getResourceAsStream(RESOURCE_PATH+"Button_4.png")
			)
			.setTextColor(Color.WHITE, Color.WHITE, Color.BLACK, Color.GRAY);
		return s;
	}
	
	public interface Loader<T> {
		void proc(T obj);
	}
	private static Font customFont = null;
	public static Font loadFont() {
		if (customFont != null) return customFont;
		try {
			customFont = Font.createFont(Font.TRUETYPE_FONT, Main.class.getResourceAsStream(RESOURCE_PATH+"font/RobotoSlab-Regular.ttf")).deriveFont(14f);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(customFont);
		} catch (IOException e) {
			Log.getLogger().warning("Не удалось загрузить шрифт. "+e.getMessage());
		} catch(FontFormatException e) {
			Log.getLogger().warning("Не удалось загрузить шрифт. Не верный формат шрифта. "+e.getMessage());
		}
		return customFont;
	}
}
