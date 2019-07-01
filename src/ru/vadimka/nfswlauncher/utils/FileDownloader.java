package ru.vadimka.nfswlauncher.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import ru.vadimka.nfswlauncher.Main;
import ru.vadimka.racingworld.encoder.Decode;

public class FileDownloader {
	
	public static final String TEMP_LINK = "https://www.dropbox.com/s/q0enm212ux5bdc8/FilesChecker.xml?dl=1";
	
	private URL url;
	
	public FileDownloader(String url) throws MalformedURLException {
		this.url = new URL(url);
	}
	
	public void download(OutputStream os) throws IOException {
		URLConnection c = url.openConnection();
		
		c.connect();
		InputStream is = c.getInputStream();
		
		byte[] buffer = new byte[1024];
		int readBytes;
		while ((readBytes = is.read(buffer)) != -1) {
			os.write(buffer, 0, readBytes);
		}
		
		is.close();
		os.close();
		
	}
	
	public void downloadAsFile(String outFilePath) throws FileNotFoundException, IOException {
		download(new FileOutputStream(outFilePath));
	}
	
	public String downloadAndDecode() throws Exception {
		File f = new File(Main.getWorkDir()+File.separator+"file_part.temp");
		downloadAsFile(f.getAbsolutePath());
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Decode.decodeFile(f, os);
		f.delete();
		
		return os.toString();
	}
}
