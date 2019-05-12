package ru.vadimka.nfswlauncher.theme.customcomponents;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

import ru.vadimka.nfswlauncher.theme.manager.StyleItem;

public class ButtonC extends JButton implements MouseListener, Stylisable {
	
	private boolean IsCustomStyle = false;

	private Boolean pressed = false;
	private Boolean entered = false;
	
	private Image Idefault = null;
	private Image Ifocus = null;
	private Image Ipressed = null;
	
	private boolean HIDE_TEXT = false;
	
	private static final long serialVersionUID = 533602725720866473L;
	
	public ButtonC(String text) {
		super(text);
		addMouseListener(this);
	}
	
	public ButtonC(String text, ActionListener act) {
		super(text);
		addMouseListener(this);
		addActionListener(act);
	}
	
	
	public void setStyle(StyleItem style) {
		if (style.getBackground() != null)
			setBackground(style.getBackground());
		if (style.getColorText() != null)
			setForeground(style.getColorText());
		HIDE_TEXT = style.textHidden();
		Idefault = style.getBackgroundDefault();
		Ifocus = style.getBackgroundFocus();
		Ipressed = style.getBackgroundPressed();
		if (Idefault != null && Ifocus != null && Ipressed != null) {
			setOpaque(false);
			setBorderPainted(false);
			IsCustomStyle = true;
		}
	}
	
	protected void paintComponent(Graphics g) {
		if (!IsCustomStyle) {
			super.paintComponent(g);
			return;
		}
		int xText = getWidth()/2-(getText().length()*(getFont().getSize()/2))/2;
		int yText = getHeight()/2+getFont().getSize()/2;
		if (!entered && !pressed && Idefault != null) {
			g.drawImage(Idefault, 0, 0, getWidth(), getHeight(), null);
			if (!HIDE_TEXT)
			g.drawString(getText(), xText, yText);
		}
		else if (pressed && Ipressed != null) {
			g.drawImage(Ipressed, 0, 0, getWidth(), getHeight(), null);
			if (!HIDE_TEXT)
			g.drawString(getText(), xText, yText);
		}
		else if (entered && !pressed && Ifocus != null) {
			g.drawImage(Ifocus, 0, 0, getWidth(), getHeight(), null);
			if (!HIDE_TEXT)
			g.drawString(getText(), xText, yText);
		} else {
			super.paintComponent(g);
		}
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		this.pressed = false;
		this.repaint();
	}
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {
		this.entered = true;
		this.repaint();
	}
	@Override
	public void mouseExited(MouseEvent e) {
		this.entered = false;
		this.repaint();
	}
	@Override
	public void mousePressed(MouseEvent e) {
		this.pressed = true;
		this.repaint();
	}
}
