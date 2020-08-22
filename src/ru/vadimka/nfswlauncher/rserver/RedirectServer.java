package ru.vadimka.nfswlauncher.rserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

import ru.vadimka.nfswlauncher.Log;

public class RedirectServer implements Runnable {
	
	protected ServerSocket SERVER;
	protected Thread SERVER_THREAD;
	
	protected String REDIRECT_HOST;
	protected int REDIRECT_PORT;
	
	protected int PORT;

	public RedirectServer(int port, String redirect_to) {
		SERVER = null;
		PORT = port;
		String[] url = redirect_to.split(":",2);
		if (url.length == 2) {
			REDIRECT_HOST = url[0];
			REDIRECT_PORT = Integer.valueOf(url[1]);
		} else {
			REDIRECT_HOST = redirect_to;
			REDIRECT_PORT = 80;
		}
	}

	public boolean isRunning() {
		if (SERVER != null && !SERVER.isClosed()) return true;
		return false;
	}

	public void run() {
		try {
			SERVER = new ServerSocket(PORT);
			Log.getLogger().info("HTTP redirect сервер запущен.");
			while(!SERVER.isClosed()) {
				new Thread(new Client(SERVER.accept(),REDIRECT_HOST,REDIRECT_PORT)).start();
			}
		} catch (IOException e) {
			Log.getLogger().log(Level.WARNING,"[HTTP redirect] Ошибка чтения",e);
		} finally {
			try {
				if (!SERVER.isClosed())
					SERVER.close();
				Log.getLogger().info("HTTP redirect сервер остановлен.");
			} catch (IOException e) {
				Log.getLogger().info("не удалось остановить HTTP redirect сервер.");
			}
		}
	}
	
	public void start() {
		SERVER_THREAD = new Thread(this);
		SERVER_THREAD.start();
	}

	public void stop() {
		try {
			SERVER.close();
			if (SERVER_THREAD.isAlive())
				SERVER_THREAD.interrupt();
		} catch (IOException e) {
			Log.getLogger().log(Level.WARNING,"[HTTP redirect] Во время выключения сервера произошла ошибка",e);
		}
	}
	public String getIP() {
		return "127.0.0.1:"+String.valueOf(PORT);
	}
	public static class Client implements Runnable {

		protected Socket socket;
		protected String host;
		protected int port;
		
		public Client(Socket socket, String host, int port) {
			this.socket = socket;
			this.host = host;
			this.port = port;
		}
		
		@Override
		public void run() {
			try {
				long start_time_ = System.currentTimeMillis();
				// Получаем данные пришедшие от отправителя
				InputStream is = socket.getInputStream();
				int available = 0;
				while (socket.isConnected() && (available = is.available()) <= 0);
				
				byte[] data = new byte[available];
				
				if (available > 1024) {
					byte[] buffer = new byte[1024];
					int readedAll = 0;
					int readed = 0;
					while (readedAll < available) {
						readed = is.read(buffer);
						System.arraycopy(buffer, 0, data, readedAll, readed);
						readedAll += readed;
					}
				} else
					is.read(data);
				
				// Подключаемся к получателю и отдаем данные отправителя
				Socket recipient = new Socket(host,port);
				
				OutputStream cos = recipient.getOutputStream();
				cos.write(data);
				cos.flush();
				
				InputStream cis = recipient.getInputStream();
				
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				
				
				byte[] bufferdata;
				byte[] buffer = new byte[1024];
				
				int readed;
				
				while (recipient.isConnected()) {
					available = cis.available();
					if (available <= 0) {
						long start_time = System.currentTimeMillis();
						while (recipient.isConnected() && cis.available() <= 0) {
							try {
								Thread.sleep(1);
							} catch (InterruptedException e) {}
							if ((System.currentTimeMillis() - start_time) > 100) break;
						}
						if (cis.available() <= 0) break;
					}
					readed = cis.read(buffer);
					bufferdata = new byte[readed];
					System.arraycopy(buffer, 0, bufferdata, 0, readed);
					bytes.write(bufferdata);
				}
				
				data = bytes.toByteArray();
				
				recipient.close();
				
				// Отдаем ответ отправителя получателю
				OutputStream os = socket.getOutputStream();
				
				os.write(data);
				os.flush();
				
				socket.close();
				Log.getLogger().info("Ответ создан за "+String.valueOf(System.currentTimeMillis()-start_time_));
			} catch (IOException e) {
				Log.getLogger().log(Level.WARNING,"Ошибка чтения, при обработке пользователя",e);
			}
		}
		
	}
}
