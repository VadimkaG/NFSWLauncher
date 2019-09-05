package ru.vadimka.nfswlauncher.theme;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JPanel;

import ru.vadimka.nfswlauncher.theme.customcomponents.Stylisable;
import ru.vadimka.nfswlauncher.theme.manager.ThemeItemNode;

public class Window extends JPanel {
	
	private static final long serialVersionUID = 132455996510111506L;
	
	private HashMap<String,Component> ELEMENTS;
	private GridBagConstraints c;
	private boolean IS_LAYOUT = false;
	private Runnable FuncUpdate = null;
	
	public Window() {
		ELEMENTS = new HashMap<String,Component>();
		setOpaque(false);
		setLayout(null);
	}
	/**
	 * Установить функцию обновления элементов окна
	 * @param func - Runnable объект обновления
	 * @return объект текущего класса
	 */
	public Window setUpdateFunc(Runnable func) {
		FuncUpdate = func;
		return this;
	}
	/**
	 * Инициализировать сетку
	 * @return объект текущего класа
	 */
	public Window addLayout() {
		IS_LAYOUT = true;
		c = new GridBagConstraints();
		setLayout(new GridBagLayout());
		return this;
	}
	/**
	 * Получить элемент экрана
	 * @param alias - строка идентифицирующая элемент
	 * @return Возвращает объект элемента
	 */
	public Component getComponent(String alias) {
		if (ELEMENTS.containsKey(alias))
			return ELEMENTS.get(alias);
		else
			return null;
	}
	/**
	 * Добавить компонент в таблицу
	 * @param component - Компонент
	 * @param node - Нода менеджера тем
	 * @param isLabel - Это label?
	 * @return объект текущего класса
	 */
	protected Window addComponent(String alias, JComponent component, ThemeItemNode node) {
		if (ELEMENTS.containsKey(alias)) return this;
		ELEMENTS.put(alias, component);
		if (IS_LAYOUT) {
			if (node.hidden()) return this;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1;
			c.weighty = 1;
			c.gridwidth = node.getWidth();
			c.gridheight = node.getHeight();
			c.gridx = node.getX();
			c.gridy = node.getY();
			add(component,c);
		} else {
			component.setBounds(node.getX(), node.getY(), node.getWidth(), node.getHeight());
			add(component);
		}
		if (component instanceof Stylisable) {
			((Stylisable) component).setStyle(node.getStyle());
		}
		return this;
	}
	/**
	 * Обновить контент
	 * @return объект текущего класса
	 */
	public Window update() {
		if (FuncUpdate != null)
			FuncUpdate.run();
		return this;
	}
}
