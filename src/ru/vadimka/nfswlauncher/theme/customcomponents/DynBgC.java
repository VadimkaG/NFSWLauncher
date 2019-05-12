package ru.vadimka.nfswlauncher.theme.customcomponents;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.Timer;

import ru.vadimka.nfswlauncher.Log;


public class DynBgC extends JComponent implements ActionListener {
	private static final long serialVersionUID = -5445012039850802608L;
	
	private BufferedImage img = null;
	private int width = 0;
	private int height = 0;
	
	private int width_cur = -1;
	private int height_cur = -1;
	
	private int width_add = 1;
	private int height_add = 1;
	
	public DynBgC(Image imagePath) {
		super();
		img = (BufferedImage) imagePath;
		width = img.getWidth();
		height = img.getHeight();
		if (width_cur == -1)
			width_cur = width/2;
		if (height_cur == -1)
			height_cur = height/2;
		rand();
		repaint();
		Timer t = new Timer(80, this);
		t.start();
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (width_cur <= 50) {
			width_add = 1;
		}
		if (height_cur <= 50) {
			height_add = 1;
		}
		
		if (width_cur >= width-getWidth()-50) {
			width_add = -1;
		}
		if (height_cur >= height-getHeight()-50) {
			height_add = -1;
		}
		
		width_cur += width_add;
		height_cur += height_add;
		repaint();
	}
	private void rand() {
		
		double rand = Math.random();
		
		if (rand > 0.5) width_add = 1;
		//else if (rand < 0.1 && height_add != 0) width_add = 0;
		else width_add = -1;
		
		rand = Math.random();
		
		if (rand > 0.5) height_add = 1;
		//else if (rand < 0.1 && width_add != 0) height_add = 0;
		else height_add = -1;
	}
	public void setImage(String imagePath) {
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					img = ImageIO.read(new File(imagePath));
					/*width = img.getWidth();
					height = img.getHeight();*/
					//this.setSize(width,height);
				} catch (IOException e) {
					Log.print("Ошибка загрузки картинки: "+imagePath);
				}
			}
		});
		th.start();
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (img != null) {
			Image i = img.getSubimage(width_cur, height_cur, getWidth(), getHeight());
			g.drawImage(i, 0, 0, getWidth(), getHeight(), null);
		}
	}
	public DynBgC setB(int x, int y, int width, int height) {
		return this;
	}
}
