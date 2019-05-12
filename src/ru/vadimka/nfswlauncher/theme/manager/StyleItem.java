package ru.vadimka.nfswlauncher.theme.manager;

import java.awt.Color;
import java.awt.Image;

public class StyleItem {
	private Color COLOR_BACKGROUND;
	private Color COLOR_TEXT;
	
	private Image BACKGROUND_DEFAULT;
	private Image BACKGROUND_PRESSED;
	private Image BACKGROUND_FOCUS;
	
	private boolean TEXT_HIDDEN;
	private boolean TEXT_CENTRED;
	
	public StyleItem() {
		COLOR_BACKGROUND = null;
		COLOR_TEXT = null;
		BACKGROUND_DEFAULT = null;
		BACKGROUND_PRESSED = null;
		BACKGROUND_FOCUS = null;
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
	 * Получить стандартный фон
	 * @return Картинка
	 */
	public Image getBackgroundDefault() {
		return BACKGROUND_DEFAULT;
	}
	/**
	 * Получить фон, когда кнопка нажата
	 * @return Картинка
	 */
	public Image getBackgroundPressed() {
		return BACKGROUND_PRESSED;
	}
	/**
	 * Получить фон, когда кноппка под фокусом курсора
	 * @return Картинка
	 */
	public Image getBackgroundFocus() {
		return BACKGROUND_FOCUS;
	}
	/**
	 * Установить картинку на фон для кнопки
	 * @param Idefault - Стандартное положение кнопки
	 * @param Ipressed - Кнопка нажата
	 * @param Ifocus - На кнеопку наведен курсор
	 */
	public void setBackground(Image Idefault, Image Ipressed, Image Ifocus) {
		BACKGROUND_DEFAULT = Idefault;
		BACKGROUND_PRESSED = Ipressed;
		BACKGROUND_FOCUS = Ifocus;
	}
	/**
	 * Установить цвет текста
	 * @param c_text - Цвет
	 */
	public void setTextColor(Color c_text) {
		COLOR_TEXT = c_text;
	}
	/**
	 * Установить цвет фона
	 * @param c_background - Цвет
	 */
	public void setBackground(Color c_background) {
		COLOR_BACKGROUND = c_background;
	}
	/**
	 * Скрыть/Показать текст
	 * @param val true = Скрыт
	 */
	public void textHide(boolean val) {
		TEXT_HIDDEN = val;
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
	public void textCenter(boolean val) {
		TEXT_CENTRED = val;
	}
	/**
	 * Проверить по середине ли текст
	 * @return true = по середине
	 */
	public boolean textCentred() {
		return TEXT_CENTRED;
	}
}
