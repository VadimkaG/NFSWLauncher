package ru.vadimka.nfswlauncher.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import ru.vadimka.nfswlauncher.Decode;
import ru.vadimka.nfswlauncher.Log;
import ru.vadimka.nfswlauncher.Main;

public class FileDownloader {
	
	private URL url;
	
	public FileDownloader(String url) throws MalformedURLException {
		this.url = new URL(url);
	}
	
	public void download(OutputStream os) throws IOException {
		InputStream is = null;
		try {
			URLConnection c = url.openConnection();
			
			c.setConnectTimeout(15 * 1000);
			
			c.connect();
			is = c.getInputStream();
			
			byte[] buffer = new byte[1024];
			int readBytes;
			while ((readBytes = is.read(buffer)) != -1) {
				os.write(buffer, 0, readBytes);
			}
			
			is.close();
			os.close();
		} catch (IOException e) {
			try {
				if (is != null)
					is.close();
				if (os != null)
					os.close();
			} catch (IOException e1) {}
			throw e;
		}
		
	}
	public InputStream download() throws IOException {
		return new Connector(url);
	}
	/**
	 * Загрузить Как файл
	 * @param outFilePath
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void downloadAsFile(String outFilePath) throws FileNotFoundException, IOException {
		download(new FileOutputStream(outFilePath));
	}
	/**
	 * Загрузить и декодировать файл
	 * @return
	 * @throws Exception
	 */
	public byte[] downloadAndDecode() throws Exception {
		File f = new File(Main.getConfigDir()+File.separator+"file_part.temp");
		downloadAsFile(f.getAbsolutePath());
		
		if (f.length() < 100) return null;
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Decode.decodeFile(f, os);
		f.delete();
		
		byte[] out = os.toByteArray();
		
		os.close();
		
		return out;
	}
	/**
	 * Скопировать файл
	 * @param source - Файл исходник
	 * @param dest - Новый файл
	 */
	public static void copyFile(File source, File dest) throws FileNotFoundException, IOException {
		if (dest.exists()) return;
		if (!dest.getParentFile().exists() && !dest.getParentFile().mkdirs()) {
			Log.getLogger().warning("[copyFile] Не удалось создать путь для "+dest.getAbsolutePath());
			return;
		}
		if (!source.exists() || !source.canRead()) return;
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				is.close();
				os.close();
			} catch (Exception e) {}
		}
	}
	
	public static class Connector extends InputStream {
		
		URLConnection c;
		InputStream is;
		
		public Connector(URL url) {
			try {
				c = url.openConnection();
				
				c.setConnectTimeout(15 * 1000);
				
				c.connect();
				is = c.getInputStream();
			} catch (IOException e) {
			}
		}
		@Override
		public int read() throws IOException {
			return is.read();
		}
		@Override
		public int read(byte[] b) throws IOException {
			return is.read(b);
		}
		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			
			return is.read(b, off, len);
		}
		
	}
}
