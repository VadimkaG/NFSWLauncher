package ru.vadimka.nfswlauncher.ValueObjects;

import java.net.MalformedURLException;
import java.util.logging.Level;

import ru.vadimka.nfswlauncher.Log;
import ru.vadimka.nfswlauncher.utils.FileDownloader;

public class RWACIndex {
	private byte[] DATA;
	private String DATA_LINK;
	private Thread downloader;
	
	public RWACIndex(String RWACIndexLink) {
		DATA_LINK = RWACIndexLink;
		DATA = null;
		downloader = null;
	}
	
	public void download() {
		downloader = new Thread(()->{
			FileDownloader fd;
			try {
				fd = new FileDownloader(
						DATA_LINK
					);
				DATA = fd.downloadAndDecode();
			} catch (MalformedURLException e) {
				Log.getLogger().warning("[RWAC] Ошибка загрузки index: Неверный формат URL");
			} catch (Exception e) {
				Log.getLogger().warning("[RWAC] Ошибка загрузки index: Файл не найден");
			}
		});
		synchronized (downloader) {
			downloader.start();
		}
	}
	
	public byte[] getData() {
		if (downloader != null && downloader.isAlive()) {
			synchronized (downloader) {
				try {
					downloader.wait();
				} catch (InterruptedException e) {
					Log.getLogger().log(Level.WARNING,"Прервана загрузка индекса",e);
				}
			}
		}
		return DATA;
	}
}
