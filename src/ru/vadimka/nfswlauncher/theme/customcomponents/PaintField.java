package ru.vadimka.nfswlauncher.theme.customcomponents;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JTextField;

public class PaintField extends JTextField {
	
	private static final long serialVersionUID = 8462998832512323877L;
	private PaintExecutor PAINT_EXECUTOR;
	
	public PaintField() {
		setOpaque(false);
		setBorder(null);
		PAINT_EXECUTOR = null;
	}
	
	public PaintField(PaintExecutor paintExecutor) {
		this();
		setPaintExecutor(paintExecutor);
	}
	
	public void setPaintExecutor(PaintExecutor paintExecutor) {
		PAINT_EXECUTOR = paintExecutor;
		repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		if (PAINT_EXECUTOR != null) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
			PAINT_EXECUTOR.paint(g2d, this);
		}
		super.paintComponent(g);
	}
	
	public static abstract class PaintExecutor {
		public abstract void paint(Graphics2D g, PaintField field);
	}

}
