package ru.vadimka.nfswlauncher.theme.customcomponents;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;

import ru.vadimka.nfswlauncher.theme.manager.StyleItem;

public class ButtonC extends JButton implements MouseListener, Stylisable {
	
	private boolean IsCustomStyle = false;

	private Boolean pressed = false;
	private Boolean entered = false;
	
	private BufferedImage Idefault = null;
	private BufferedImage Ifocus = null;
	private BufferedImage Ipressed = null;
	private BufferedImage Idisabled = null;
	
	private Color TEXT_COLOR_DEFAULT;
	private Color TEXT_COLOR_PRESSED;
	private Color TEXT_COLOR_FOCUS;
	private Color TEXT_COLOR_DISABLED;
	
	private boolean HIDE_TEXT = false;
	
	private static final long serialVersionUID = 533602725720866473L;
	
	public ButtonC() {
		super("");
		addMouseListener(this);
	}
	
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
		TEXT_COLOR_DEFAULT = style.getColorText();
		TEXT_COLOR_PRESSED = style.getColorTextPressed();
		TEXT_COLOR_FOCUS = style.getColorTextFocus();
		TEXT_COLOR_DISABLED = style.getColorTextDisabled();
		HIDE_TEXT = style.textHidden();
		Idefault = style.getBackgroundDefault();
		Ifocus = style.getBackgroundFocus();
		Ipressed = style.getBackgroundPressed();
		Idisabled = style.getBackgroundDisabled();
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
		Graphics2D gt = (Graphics2D) g;
		gt.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int xText = getWidth()/2-g.getFontMetrics().stringWidth(getText())/2;
		int yText = getHeight()/2+gt.getFont().getSize()/2;
		if (!isEnabled() && Idisabled != null) {
			if (TEXT_COLOR_DISABLED != null) setForeground(TEXT_COLOR_DISABLED);
			gt.drawImage(Idisabled, 0, 0, getWidth(), getHeight(), null);
			if (!HIDE_TEXT)
				gt.drawString(getText(), xText, yText);
			return;
		}
		if (!entered && !pressed && Idefault != null) {
			if (TEXT_COLOR_DEFAULT != null) setForeground(TEXT_COLOR_DEFAULT);
			gt.drawImage(Idefault, 0, 0, getWidth(), getHeight(), null);
			if (!HIDE_TEXT)
			gt.drawString(getText(), xText, yText);
		}
		else if (pressed && Ipressed != null) {
			if (TEXT_COLOR_PRESSED != null) setForeground(TEXT_COLOR_PRESSED);
			gt.drawImage(Ipressed, 0, 0, getWidth(), getHeight(), null);
			if (!HIDE_TEXT)
			gt.drawString(getText(), xText, yText);
		}
		else if (entered && !pressed && Ifocus != null) {
			if (TEXT_COLOR_FOCUS != null) setForeground(TEXT_COLOR_FOCUS);
			gt.drawImage(Ifocus, 0, 0, getWidth(), getHeight(), null);
			if (!HIDE_TEXT)
			gt.drawString(getText(), xText, yText);
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
