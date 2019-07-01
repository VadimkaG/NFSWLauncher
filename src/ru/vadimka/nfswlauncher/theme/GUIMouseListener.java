package ru.vadimka.nfswlauncher.theme;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;

import ru.vadimka.nfswlauncher.Log;

public class GUIMouseListener implements MouseListener, MouseMotionListener {
	
	// настройки
	private int SCROLL_ZONE_X = 0;
	private int SCROLL_ZONE_Y = 0;
	private int SCROLL_ZONE_W = 800;
	private int SCROLL_ZONE_H = 30;
	
	// /настройки
	
	private JFrame THEME;
	
	public GUIMouseListener(JFrame obj) {
		THEME = obj;
	}
	
	private Point clickPoint;
	@Override
	public void mouseDragged(MouseEvent e) {
		if (clickPoint == null) {
			Log.getLogger().warning("Точка клика не установлена!");
			return;
		}
		if (
				SCROLL_ZONE_X <= clickPoint.x &&
				SCROLL_ZONE_Y <= clickPoint.y &&
				SCROLL_ZONE_X+SCROLL_ZONE_W >= clickPoint.x &&
				SCROLL_ZONE_Y+SCROLL_ZONE_H >= clickPoint.y
				) {
			int thisX = THEME.getLocation().x;
			int thisY = THEME.getLocation().y;
			
			int xMoved = (thisX + e.getX()) - (thisX + clickPoint.x);
			int yMoved = (thisY + e.getY()) - (thisY + clickPoint.y);
			
			THEME.setLocation(thisX + xMoved, thisY + yMoved);
		}
	}
	@Override
	public void mouseMoved(MouseEvent arg0) {}
	@Override
	public void mouseClicked(MouseEvent arg0) {}
	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}
	@Override
	public void mousePressed(MouseEvent e) {
		clickPoint = e.getPoint();
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {}
}
