package ru.vadimka.nfswlauncher.theme.manager;

public class ThemeItemNode {
	private int X;
	private int Y;
	private int W;
	private int H;
	
	private StyleItem STYLE;
	
	private boolean HIDE_ELEMENT;
	
	public ThemeItemNode(int x, int y, int width, int height) {
		X = x;
		Y = y;
		W = width;
		H = height;
		HIDE_ELEMENT = false;
	}
	/**
	 * Получить координату ширины
	 * @return
	 */
	public int getX() {
		return X;
	}
	/**
	 * Получить координату высоты
	 * @return
	 */
	public int getY() {
		return Y;
	}
	/**
	 * Получить ширину элемента
	 * @return
	 */
	public int getWidth() {
		return W;
	}
	/**
	 * Получить высоту элемента
	 * @return
	 */
	public int getHeight() {
		return H;
	}
	/**
	 * Установить стиль элемента
	 * @param style
	 */
	public void setStyle(StyleItem style) {
		STYLE = style;
	}
	/**
	 * Получить стиль элемента
	 * @return
	 */
	public StyleItem getStyle() {
		if (STYLE == null) STYLE = new StyleItem();
		return STYLE;
	}
	/**
	 * Скрыть элемент
	 * @param val - true = Скрыть
	 */
	public void hide(boolean val) {
		HIDE_ELEMENT = val;
	}
	/**
	 * Скрыт ли элемент
	 * @return true = Скрыт
	 */
	public boolean hidden() {
		return HIDE_ELEMENT;
	}
}
