package ru.vadimka.nfswlauncher.theme;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import ru.vadimka.nfswlauncher.Log;
import ru.vadimka.nfswlauncher.theme.manager.StyleItem;
import ru.vadimka.nfswlauncher.utils.AsyncTasksUtils;

public abstract class GUIResourseLoader {
	
	private static final String RESOURCE_PATH = "/ru/vadimka/nfswlauncher/resources/theme/";
	
	public static void loadIcon(Loader<Image> loader) {
		AsyncTasksUtils.addTask(new AsyncTasksUtils.Task(() -> {
			loader.proc(Toolkit.getDefaultToolkit().getImage(Class.class.getResource(RESOURCE_PATH+"icon.png")));
		}));
	}
	
	private static BufferedImage backgorund_image = null;
	
	public static void loadBackground(Loader<BufferedImage> loader) {
		if (backgorund_image != null) {
			loader.proc(backgorund_image);
			return;
		}
		AsyncTasksUtils.addTask(new AsyncTasksUtils.Task(() -> {
			try {
				backgorund_image = ImageIO.read(Class.class.getResource(RESOURCE_PATH+"background.png"));
				loader.proc(backgorund_image);
			} catch (IOException e) {
				Log.getLogger().warning("Не удалось загрузить фон");
			}
		}));
	}
	
	public static void loadMap(Loader<BufferedImage> loader) {
		AsyncTasksUtils.addTask(new AsyncTasksUtils.Task(() -> {
			try {
				BufferedImage i = ImageIO.read(Class.class.getResource(RESOURCE_PATH+"worldmap.png"));
				loader.proc(i);
			} catch (IOException e) {
				Log.getLogger().warning("Не удалось загрузить карту");
			}
		}));
	}
	
	public static void loadUpBar(Loader<BufferedImage> loader) {
		AsyncTasksUtils.addTask(new AsyncTasksUtils.Task(() -> {
			try {
				BufferedImage i = ImageIO.read(Class.class.getResource(RESOURCE_PATH+"status_bar.png"));
				loader.proc(i);
			} catch (IOException e) {
				Log.getLogger().warning("Не удалось загрузить верхний бар");
			}
		}));
	}
	
	private static BufferedImage Block = null;
	
	public static void getBlock(Loader<BufferedImage> loader) {
		if (Block != null) {
			loader.proc(Block);
			return;
		}
		AsyncTasksUtils.addTask(new AsyncTasksUtils.Task(() -> {
			try {
				BufferedImage i = ImageIO.read(Class.class.getResource(RESOURCE_PATH+"block.png"));
				loader.proc(i);
			} catch (IOException e) {
				Log.getLogger().warning("Не удалось загрузить задний блок");
			}
		}));
	}
	
	public static void loadLeftBar(Loader<BufferedImage> loader) {
		AsyncTasksUtils.addTask(new AsyncTasksUtils.Task(() -> {
			try {
				BufferedImage i = ImageIO.read(Class.class.getResource(RESOURCE_PATH+"bar_left.png"));
				loader.proc(i);
			} catch (IOException e) {
				Log.getLogger().warning("Не удалось загрузить фон левого бара");
			}
		}));
	}
	
	public static void loadStyleBtnExit(Loader<StyleItem> loader) {
		AsyncTasksUtils.addTask(new AsyncTasksUtils.Task(() -> {
			try {
				StyleItem s = new StyleItem().setBackground(
						ImageIO.read(Class.class.getResourceAsStream(RESOURCE_PATH+"btn_exit.png")), 
						ImageIO.read(Class.class.getResourceAsStream(RESOURCE_PATH+"btn_exit_pressed.png")), 
						ImageIO.read(Class.class.getResourceAsStream(RESOURCE_PATH+"btn_exit_focus.png"))
					);
				loader.proc(s);
			} catch (IOException e) {
				Log.getLogger().warning("Не удалось загрузить иконку выхода");
			}
		}));
	}
	
	public static void loadStyleBtnHide(Loader<StyleItem> loader) {
		AsyncTasksUtils.addTask(new AsyncTasksUtils.Task(() -> {
			try {
				StyleItem s = new StyleItem().setBackground(
						ImageIO.read(Class.class.getResourceAsStream(RESOURCE_PATH+"btn_hide.png")), 
						ImageIO.read(Class.class.getResourceAsStream(RESOURCE_PATH+"btn_hide_pressed.png")), 
						ImageIO.read(Class.class.getResourceAsStream(RESOURCE_PATH+"btn_hide_focus.png"))
					);
				loader.proc(s);
			} catch (IOException e) {
				Log.getLogger().warning("Не удалось загрузить кнопку скрытия окна");
			}
		}));
	}
	
	private static Image TAB1 = null;
	
	public static void loadTabFirst(Loader<Image> loader) {
		if (TAB1 != null) {
			loader.proc(TAB1);
			return;
		}
		AsyncTasksUtils.addTask(new AsyncTasksUtils.Task(() -> {
			try {
				TAB1 = ImageIO.read(Class.class.getResource(RESOURCE_PATH+"tab1.png"));
				loader.proc(TAB1);
			} catch (IOException e) {
				Log.getLogger().warning("Не удалось загрузить фон вкладки");
			}
		}));
	}
	
	private static StyleItem discordBtn = null;
	
	public static void loadDiscordBtn(Loader<StyleItem> loader) {
		if (discordBtn == null) {
			AsyncTasksUtils.addTask(new AsyncTasksUtils.Task(() -> {
				try {
					BufferedImage discordImage = ImageIO.read(Class.class.getResource(RESOURCE_PATH+"Discord_off.png"));
					BufferedImage discordImage_pressed = ImageIO.read(Class.class.getResource(RESOURCE_PATH+"Discord_on.png"));
					discordBtn = new StyleItem().setBackground(discordImage, discordImage_pressed, discordImage_pressed, discordImage).textHide(true);
					loader.proc(discordBtn);
				} catch (IOException e) {
					Log.getLogger().warning("Не удалось загрузить кнопку дискорда");
				}
			}));
		} else
			loader.proc(discordBtn);
	}
	
	private static StyleItem siteBtn = null;
	
	public static void loadSiteBtn(Loader<StyleItem> loader) {
		if (siteBtn == null) {
			AsyncTasksUtils.addTask(new AsyncTasksUtils.Task(() -> {
				try {
					BufferedImage siteImage = ImageIO.read(Class.class.getResource(RESOURCE_PATH+"Site_off.png"));
					BufferedImage siteImage_pressed = ImageIO.read(Class.class.getResource(RESOURCE_PATH+"Site_on.png"));
					siteBtn = new StyleItem().setBackground(siteImage, siteImage_pressed, siteImage_pressed, siteImage).textHide(true);
					loader.proc(siteBtn);
				} catch (IOException e) {
					Log.getLogger().warning("Не удалось загрузить кнопку сайта");
				}
			}));
		} else
			loader.proc(siteBtn);
	}
	
	private static StyleItem serversBtn = null;
	
	public static void loadServersBtn(Loader<StyleItem> loader) {
		if (serversBtn == null) {
			AsyncTasksUtils.addTask(new AsyncTasksUtils.Task(() -> {
				try {
					BufferedImage serversImage = ImageIO.read(Class.class.getResource(RESOURCE_PATH+"servers_on.png"));
					BufferedImage serversImageFocus = ImageIO.read(Class.class.getResource(RESOURCE_PATH+"servers_mouse.png"));
					BufferedImage serversImageOff = ImageIO.read(Class.class.getResource(RESOURCE_PATH+"servers_off.png"));
					serversBtn = new StyleItem().setBackground(serversImage, serversImageOff, serversImageFocus, serversImageOff).textHide(true);
					loader.proc(serversBtn);
				} catch (IOException e) {
					Log.getLogger().warning("Не удалось загрузить кнопку серверов");
				}
			}));
		} else
			loader.proc(serversBtn);
	}
	
	private static StyleItem settingsBtn = null;
	
	public static void loadSettingsBtn(Loader<StyleItem> loader) {
		if (settingsBtn == null) {
			AsyncTasksUtils.addTask(new AsyncTasksUtils.Task(() -> {
				try {
					BufferedImage sttingsImage = ImageIO.read(Class.class.getResource(RESOURCE_PATH+"setting_on.png"));
					BufferedImage settingsImageFocus = ImageIO.read(Class.class.getResource(RESOURCE_PATH+"setting_mouse.png"));
					BufferedImage settingsImageOff = ImageIO.read(Class.class.getResource(RESOURCE_PATH+"setting_off.png"));
					settingsBtn = new StyleItem().setBackground(sttingsImage, settingsImageOff, settingsImageFocus, settingsImageOff).textHide(true);
					loader.proc(settingsBtn);
				} catch (IOException e) {
					Log.getLogger().warning("Не удалось загрузить кнопку настроек");
				}
			}));
		} else
			loader.proc(settingsBtn);
	}
	
	private static StyleItem infoBtn = null;
	
	public static void loadInfoBtn(Loader<StyleItem> loader) {
		if (infoBtn == null) {
			AsyncTasksUtils.addTask(new AsyncTasksUtils.Task(() -> {
				try {
					BufferedImage infoImage = ImageIO.read(Class.class.getResource(RESOURCE_PATH+"info_on.png"));
					BufferedImage infoImageFocus = ImageIO.read(Class.class.getResource(RESOURCE_PATH+"info_mouse.png"));
					BufferedImage infoImageOff = ImageIO.read(Class.class.getResource(RESOURCE_PATH+"info_off.png"));
					infoBtn = new StyleItem().setBackground(infoImage, infoImageOff, infoImageFocus, infoImageOff).textHide(true);
					loader.proc(infoBtn);
				} catch (IOException e) {
					Log.getLogger().warning("Не удалось загрузить кнопку инфо");
				}
			}));
		} else
			loader.proc(infoBtn);
	}
	
	private static StyleItem startBtn;
	
	public static void loadBtn(Loader<StyleItem> loader) {
		if (startBtn == null) {
			AsyncTasksUtils.addTask(new AsyncTasksUtils.Task(() -> {
				try {
					BufferedImage startImage = ImageIO.read(Class.class.getResource(RESOURCE_PATH+"Button_1.png"));
					BufferedImage startImageFocus = ImageIO.read(Class.class.getResource(RESOURCE_PATH+"Button_3.png"));
					BufferedImage startImagePressed = ImageIO.read(Class.class.getResource(RESOURCE_PATH+"Button_2.png"));
					BufferedImage startImageDisabled = ImageIO.read(Class.class.getResource(RESOURCE_PATH+"Button_4.png"));
					startBtn = new StyleItem().setBackground(startImage, startImagePressed, startImageFocus, startImageDisabled).setTextColor(Color.WHITE, Color.WHITE, Color.BLACK, Color.GRAY);
					loader.proc(startBtn);
				} catch (IOException e) {
					Log.getLogger().log(Level.WARNING,"Не удалось загрузить кнопку", e);
				}
			}));
		} else
			loader.proc(startBtn);
	}
	
	public interface Loader<T> {
		void proc(T obj);
	}
}
