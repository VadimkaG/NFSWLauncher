package ru.vadimka.nfswlauncher.theme.customcomponents;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

public class PaintButton extends JPanel implements MouseListener {

	private static final long serialVersionUID = 6532604797704350718L;
	
	private boolean PRESSED;
	private boolean ENTERED;
	private boolean ENABLED;
	private String TEXT;
	private ActionListener ACTION_LISTENER;
	
	private PaintExecutor paintExecutor = null;
	
	public PaintButton() {
		PRESSED = false;
		ENTERED = false;
		ENABLED = true;
		TEXT = "";
		ACTION_LISTENER = null;
		addMouseListener(this);
	}
	
	public PaintButton(PaintExecutor exe) {
		this();
		setPaintExecutor(exe);
	}
	
	public PaintButton(String text, PaintExecutor exe) {
		this();
		setText(text);
		setPaintExecutor(exe);
	}
	/**
	 * Установить текст кнопки
	 * @param text - Текст, который будет отображаться
	 */
	public void setText(String text) {
		TEXT = text;
	}
	/**
	 * Получить текст кнопки
	 */
	public String getText() {
		return TEXT;
	}
	/**
	 * Кнопка нажата
	 */
	public boolean isPressed() {
		return PRESSED;
	}
	/**
	 * Мышка наведена на кнопку
	 */
	public boolean isEntered() {
		return ENTERED;
	}
	/**
	 * Включить/Выключить кнопку
	 */
	public void setEnabled(boolean enabled) {
		ENABLED = enabled;
	}
	/**
	 * Включена ли кнопка
	 */
	public boolean isEnabled() {
		return ENABLED;
	}
	/**
	 * Установить Функцию, которая будет заниматься отрисовкой объекта
	 * @param exe
	 */
	public void setPaintExecutor(PaintExecutor exe) {
		paintExecutor = exe;
		setOpaque(false);
	}
	/**
	 * Установить действие при нажатии
	 * @param action - Вызовется, когда отпустят кнопку
	 */
	public void addActionListener(ActionListener action) {
		ACTION_LISTENER = action;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		if (paintExecutor != null) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
			paintExecutor.paint(g2d, this);
		} else
			super.paintComponent(g);
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		PRESSED = false;
		ENTERED = false;
		repaint();
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		if (ACTION_LISTENER != null)
			ACTION_LISTENER.actionPerformed(new ActionEvent(e,0,"Кнопка нажата"));
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		ENTERED = true;
		repaint();
	}
	@Override
	public void mouseExited(MouseEvent e) {
		ENTERED = false;
		repaint();
	}
	@Override
	public void mousePressed(MouseEvent e) {
		PRESSED = true;
		repaint();
	}
	
	public static abstract class PaintExecutor {
		public abstract void paint(Graphics2D g, PaintButton button);
	}
}
