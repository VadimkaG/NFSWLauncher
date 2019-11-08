package ru.vadimka.nfswlauncher.theme;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;

public class GUIMouseMovement implements MouseListener, MouseMotionListener {
	
	private int SCROLL_ZONE_X;
	private int SCROLL_ZONE_Y;
	private int SCROLL_ZONE_X2;
	private int SCROLL_ZONE_Y2;
	
	private int thisX;
	private int thisY;
	
	private int clicked_x;
	private int clicked_y;
	private boolean active;
	
	private JFrame THEME;
	
	public GUIMouseMovement(JFrame obj, int x, int y, int w, int h) {
		THEME = obj;
		SCROLL_ZONE_X = x;
		SCROLL_ZONE_Y = y;
		SCROLL_ZONE_X2 = x+w;
		SCROLL_ZONE_Y2 = y+h;
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		if (!active) return;
		THEME.setLocation(thisX + (e.getXOnScreen() - clicked_x), thisY + (e.getYOnScreen() - clicked_y));
	}
	@Override
	public void mouseMoved(MouseEvent e) {}
	@Override
	public void mouseClicked(MouseEvent arg0) {}
	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}
	@Override
	public void mousePressed(MouseEvent e) {
		if (
				SCROLL_ZONE_X <= e.getX() &&
				SCROLL_ZONE_Y <= e.getY() &&
				SCROLL_ZONE_X2 >= e.getX() &&
				SCROLL_ZONE_Y2 >= e.getY()
				) {
			thisX = THEME.getX();
			thisY = THEME.getY();
			
			clicked_x = thisX + e.getX();
			clicked_y = thisY + e.getY();
			
			active = true;
		}
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		active = false;
	}
}
