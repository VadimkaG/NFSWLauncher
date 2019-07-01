package ru.vadimka.nfswlauncher.theme.manager;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class StyleItem {
	private Color COLOR_BACKGROUND;
	private Color COLOR_TEXT;
	private Color COLOR_TEXT_PRESSED;
	private Color COLOR_TEXT_FOCUS;
	private Color COLOR_TEXT_DISABLED;
	
	private BufferedImage BACKGROUND_DEFAULT;
	private BufferedImage BACKGROUND_PRESSED;
	private BufferedImage BACKGROUND_FOCUS;
	private BufferedImage BACKGROUND_DISABLED;
	
	private boolean TEXT_HIDDEN;
	private boolean TEXT_CENTRED;
	
	public StyleItem() {
		COLOR_BACKGROUND = null;
		COLOR_TEXT = null;
		BACKGROUND_DEFAULT = null;
		BACKGROUND_PRESSED = null;
		BACKGROUND_FOCUS = null;
		BACKGROUND_DISABLED = null;
		TEXT_HIDDEN = false;
		TEXT_CENTRED = false;
	}
	/**
	 * Получить цвет фона
	 * @return
	 */
	public Color getBackground() {
		return COLOR_BACKGROUND;
	}
	/**
	 * Получить цвет текста
	 * @return
	 */
	public Color getColorText() {
		return COLOR_TEXT;
	}
	/**
	 * Получить цвет текста, когда кнопка нажата
	 * @return
	 */
	public Color getColorTextPressed() {
		return COLOR_TEXT_PRESSED;
	}
	/**
	 * Получить цвет текста, когда кнопка под фокусом
	 * @return
	 */
	public Color getColorTextFocus() {
		return COLOR_TEXT_FOCUS;
	}
	/**
	 * Получить цвет текста, когда кнопка выключена
	 * @return
	 */
	public Color getColorTextDisabled() {
		return COLOR_TEXT_DISABLED;
	}
	/**
	 * Получить стандартный фон
	 * @return Картинка
	 */
	public BufferedImage getBackgroundDefault() {
		return BACKGROUND_DEFAULT;
	}
	/**
	 * Получить фон, когда кнопка нажата
	 * @return Картинка
	 */
	public BufferedImage getBackgroundPressed() {
		return BACKGROUND_PRESSED;
	}
	/**
	 * Получить фон, когда кноппка под фокусом курсора
	 * @return Картинка
	 */
	public BufferedImage getBackgroundFocus() {
		return BACKGROUND_FOCUS;
	}
	/**
	 * Получить фон, когда кнопка выключена
	 * @return Картинка
	 */
	public BufferedImage getBackgroundDisabled() {
		return BACKGROUND_DISABLED;
	}
	/**
	 * Установить картинку на фон для кнопки
	 * @param Idefault - Стандартное положение кнопки
	 * @param Ipressed - Кнопка нажата
	 * @param Ifocus - На кнеопку наведен курсор
	 */
	public StyleItem setBackground(BufferedImage Idefault, BufferedImage Ipressed, BufferedImage Ifocus) {
		BACKGROUND_DEFAULT = Idefault;
		BACKGROUND_PRESSED = Ipressed;
		BACKGROUND_FOCUS = Ifocus;
		return this;
	}
	/**
	 * Установить картинку на фон для кнопки
	 * @param Idefault - Стандартное положение кнопки
	 * @param Ipressed - Кнопка нажата
	 * @param Ifocus - На кнеопку наведен курсор
	 * @param Idisabled - Выключенное состояние кнопки
	 */
	public StyleItem setBackground(BufferedImage Idefault, BufferedImage Ipressed, BufferedImage Ifocus, BufferedImage Idisabled) {
		BACKGROUND_DISABLED = Idisabled;
		return setBackground(Idefault, Ipressed, Ifocus);
	}
	/**
	 * Установить цвет текста
	 * @param c_text - Цвет
	 */
	public StyleItem setTextColor(Color c_text) {
		COLOR_TEXT = c_text;
		COLOR_TEXT_PRESSED = c_text;
		COLOR_TEXT_FOCUS = c_text;
		COLOR_TEXT_DISABLED = c_text;
		return this;
	}
	/**
	 * Установить цвет текста для кнопки с разыми цветами
	 * @param def - Стандартный цвет
	 * @param pressed - Цвет. когда кнопка нажата
	 * @param focus - Цвет когда кнопка под фокусом
	 * @param disabled - Цвет. когда кнопка выключена
	 */
	public StyleItem setTextColor(Color def, Color pressed, Color focus, Color disabled) {
		COLOR_TEXT = def;
		COLOR_TEXT_PRESSED = pressed;
		COLOR_TEXT_FOCUS = focus;
		COLOR_TEXT_DISABLED = disabled;
		return this;
	}
	/**
	 * Установить цвет фона
	 * @param c_background - Цвет
	 */
	public StyleItem setBackground(Color c_background) {
		COLOR_BACKGROUND = c_background;
		return this;
	}
	/**
	 * Скрыть/Показать текст
	 * @param val true = Скрыт
	 */
	public StyleItem textHide(boolean val) {
		TEXT_HIDDEN = val;
		return this;
	}
	/**
	 * Скрыт ли текст
	 * @return true = Скрыт
	 */
	public boolean textHidden() {
		return TEXT_HIDDEN;
	}
	/**
	 * Установить текст по центру
	 * @param val - true = отцентровать
	 */
	public StyleItem textCenter(boolean val) {
		TEXT_CENTRED = val;
		return this;
	}
	/**
	 * Проверить по середине ли текст
	 * @return true = по середине
	 */
	public boolean textCentred() {
		return TEXT_CENTRED;
	}
}
