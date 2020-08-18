package ru.vadimka.nfswlauncher.theme.customcomponents;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JComponent;

import ru.vadimka.nfswlauncher.Log;


public class ImageC extends JComponent {
	private static final long serialVersionUID = -5445012039850802608L;
	
	private BufferedImage img;
	/*private Integer width = 0;
	private Integer height = 0;*/
	
	public ImageC(Image image) {
		super();
		setImage(image);
	}
	public ImageC() {
		super();
		img = null;
	}
	public ImageC(InputStream imageUrl, int x, int y) {
		setImage(imageUrl, x, y);
	}
	public void setImage(Image image) {
		img = (BufferedImage) image;
		repaint();
	}
	public void setImage(InputStream imageUrl,int x, int y) {
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					img = subsampleImage(ImageIO.createImageInputStream(imageUrl), x, y);
					repaint();
				} catch (IOException e) {
					Log.getLogger().warning("Ошибка загрузки картинки");
				}
			}
		});
		th.start();
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
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
	public static BufferedImage subsampleImage(
			ImageInputStream inputStream,
			int x,
			int y//,
			//IIOReadProgressListener progressListener
	) throws IOException {
		BufferedImage resampledImage = null;
		
		Iterator<ImageReader> readers = ImageIO.getImageReaders(inputStream);
		
		if(!readers.hasNext()) {
			throw new IOException("No reader available for supplied image stream.");
		}
		
		ImageReader reader = readers.next();
		
		ImageReadParam imageReaderParams = reader.getDefaultReadParam();
		reader.setInput(inputStream);
		
		Dimension d1 = new Dimension(reader.getWidth(0), reader.getHeight(0));
		Dimension d2 = new Dimension(x, y);
		int subsampling = (int)scaleSubsamplingMaintainAspectRatio(d1, d2);
		imageReaderParams.setSourceSubsampling(subsampling, subsampling, 0, 0);
		
		//reader.addIIOReadProgressListener(progressListener);
		resampledImage = reader.read(0, imageReaderParams);
		reader.removeAllIIOReadProgressListeners();
		
		return resampledImage;
	}
	public static long scaleSubsamplingMaintainAspectRatio(Dimension d1, Dimension d2) {
		long subsampling = 1;
		
		if(d1.getWidth() > d2.getWidth()) {
			subsampling = Math.round(d1.getWidth() / d2.getWidth());
		} else if(d1.getHeight() > d2.getHeight()) {
			subsampling = Math.round(d1.getHeight() / d2.getHeight());
		}
		
		return subsampling;
	}
}
