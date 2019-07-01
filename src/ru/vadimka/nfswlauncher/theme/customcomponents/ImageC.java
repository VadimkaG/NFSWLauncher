package ru.vadimka.nfswlauncher.theme.customcomponents;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import ru.vadimka.nfswlauncher.Log;


public class ImageC extends JComponent {
	private static final long serialVersionUID = -5445012039850802608L;
	
	private BufferedImage img = null;
	/*private Integer width = 0;
	private Integer height = 0;*/
	
	public ImageC(String imagePath) {
		super();
		setImage(imagePath);
	}
	public ImageC(Image image) {
		super();
		img = (BufferedImage) image;
		repaint();
	}
	public ImageC() {
		super();
		img = null;
	}
	public void setURL(String url) {
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					img = ImageIO.read(new URL(url));
					repaint();
				} catch (IOException e) {
					Log.getLogger().warning("Ошибка загрузки картинки: "+url);
				}
			}
		});
		th.start();
	}
	public void setImage(Image image) {
		img = (BufferedImage) image;
		repaint();
	}
	public void setImage(String imagePath) {
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					img = ImageIO.read(new File(imagePath));
					repaint();
					/*width = img.getWidth();
					height = img.getHeight();*/
					//this.setSize(width,height);
				} catch (IOException e) {
					Log.getLogger().warning("Ошибка загрузки картинки: "+imagePath);
				}
			}
		});
		th.start();
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (img != null)
			g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
	}
	public ImageC setB(int x, int y, int width, int height) {
		return this;
	}
	/*public void resizeImage(Integer i) {
		width /= i;
		height /= i;
		this.repaint();
	}
	public void resizeImage(Integer Width, Integer Height) {
		width = Width;
		height = Height;
		this.repaint();
	}*/
}
