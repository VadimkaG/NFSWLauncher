package ru.vadimka.nfswlauncher.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;

import ru.vadimka.nfswlauncher.Config;
import ru.vadimka.nfswlauncher.Log;

public class HTTPRequest {
	private HttpURLConnection conn;
	private URL url;
	private byte[] postData;
	private String CHARSET;
	public static final String DELIMER_FIRST = "?";
	public static final String DELIMER = "&";
	
	private Boolean n;
	private Boolean output;
	private Boolean gzip;
	private long startTime = 0;
	private long connectedTime = 0;
	private Thread requestThread = null;
	
	private boolean error = false;
	
	private Action actionAfterRequest = null;
	
	private static final boolean DEBUG = false;
	
	private String USER_AGENT = "Mozilla/5.0 ("+System.getProperty("os.name")+" "+System.getProperty("os.arch")+") "+Config.WINDOW_TITLE+" "+Config.VERSION;
	
	/**
	 * HTTP запрос
	 * @param URL - Ссылка на страницу
	 * @param params - параметры запроса
	 * @param methodPost - Если true, то запрос POST. Если false, запрос GET
	 */
	public HTTPRequest(String URL, String params, Boolean methodPost) {
		n = false;
		gzip = false;
		try {
			url = new URL(URL);
			postData = params.getBytes( StandardCharsets.UTF_8 );
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			//conn.setDoInput(true);
			output = methodPost;
			conn.setDoOutput(output);
			conn.setUseCaches(false);
			conn.setRequestProperty("User-Agent", USER_AGENT);
			if (methodPost) {
				conn.setRequestMethod("POST");
			} else {
				conn.setRequestMethod("GET");
			}
			CHARSET = "";
		} catch (MalformedURLException e) {
			Log.getLogger().warning("Ошибка: Не корректная ссылка: "+URL);
		} catch (IOException e) {
			Log.getLogger().warning("Ошибка: Не удалось создать соединение с "+URL+". "+e.getMessage());
		} catch (Exception e) {
			Log.getLogger().log(Level.WARNING,"Ошибка: Непредвиденная ошибка инициализации соединения с "+URL,e);
		}
	}
	/**
	 * HTTP запрос
	 * @param URL - Ссылка на старницу
	 */
	public HTTPRequest(String URL) {
		n = false;
		gzip = false;
		try {
			url = new URL(URL);
			postData = "".getBytes( StandardCharsets.UTF_8 );
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			//conn.setDoInput(true);
			output = false;
			//conn.setDoOutput(output);
			conn.setUseCaches(false);
			conn.setRequestProperty("User-Agent", USER_AGENT);
			conn.setRequestMethod("GET");
			CHARSET = "";
		} catch (MalformedURLException e) {
			Log.getLogger().warning("Ошибка: Не корректная ссылка: "+URL);
		} catch (IOException e) {
			Log.getLogger().warning("Ошибка: Не удалось создать соединение с "+URL+". "+e.getMessage());
		} catch (Exception e) {
			Log.getLogger().warning("Ошибка: Непредвиденная ошибка инициализации соединения с "+URL);
		}
	}
	/**
	 * Установить юзер-агент
	 * @param agent
	 */
	public void setUserAgent(String agent) {
		USER_AGENT = agent;
	}
	/**
	 * Установить кодировку передаваемых данных
	 * По умолчанию - UTF-8
	 * @param charset - Название Кодировки
	 */
	public void setCharset(String charset) {
		CHARSET = charset;
		//conn.setRequestProperty("charset", charset);
		conn.setRequestProperty("Content-Type", "text/html; charset="+charset);
	}
	/**
	 * Установить заголовок для запроса
	 * @param alias - Название заголовка
	 * @param value - Значение заголовка
	 */
	public void setHeader(String alias, String value) {
		conn.setRequestProperty(alias, value);
	}
	/**
	 * Установить тип контента
	 * @param type - тип контента
	 */
	public void setContentType(String type) {
		conn.setRequestProperty("Content-Type", type);
	}
	/**
	 * Выводить ли \n в конце каждой строки
	 * @param value
	 */
	public void setN(Boolean value) {
		n = value;
	}
	/**
	 * Включить сжатие
	 * @param gzip - Включить
	 */
	public void setGZIP(Boolean gzip) {
		this.gzip = gzip;
	}
	/**
	 * Запрос
	 * @return Ответ
	 */
	public void proc() {
		if (requestThread != null) return;
		if (url == null || url.getHost().equalsIgnoreCase("")) {
			Log.getLogger().warning("[HTTPRequest] Остановлена попытка выполнить HTTP запрос c NULL url");
			return;
		}
		if (DEBUG) Log.getLogger().info("[HTTPRequest] Запрос \""+url.getHost()+"\" запущен.\nПолная ссылка: "+url.getHost()+url.getPath());
		startTime = System.currentTimeMillis();
		
		requestThread = new Thread(new RequestConnect(this,actionAfterRequest));
		requestThread.start();
		
	}
	public boolean waitResponse() {
		if (requestThread == null)
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {};
		if (!requestThread.isAlive()) return true;
		if (requestThread != null) {
			synchronized (requestThread) {
				try {
					requestThread.wait(/*3000*/);
//					if (requestThread.isAlive()) {
//						requestThread.interrupt();
//						Log.getLogger().warning("[HTTPRequest] Не удалось дождаться запроса \""+url.getHost()+url.getPath()+"\".");
//					}
					if (error) return false;
					return true;
				} catch (InterruptedException e) {
					Log.getLogger().log(Level.WARNING,"[HTTPRequest] Запрос приостановлен...",e);
				}
			}
		}
		return false;
	}
	/**
	 * Получить код ответа от сервера
	 * @return - Код ответа
	 */
	public int getResponseCode() {
		if (error) return 0;
		if (!waitResponse()) {
			if (DEBUG) Log.getLogger().warning("[HTTPRequest] Ожидание кода ответа вернуло false");
			return 0;
		}
		return rawgetResponseCode();
	}
	private int rawgetResponseCode() {
		try {
			return conn.getResponseCode();
		} catch (IOException e) {
			Log.getLogger().warning("Ошибка: Не удалось получить код ответа от "+url.getHost()+url.getPath()+". "+e.getMessage());
			return 0;
		} catch (Exception e) {
			Log.getLogger().warning("Ошибка: Непредвиденная ошибка при получении кода ответа от "+conn.getURL().toString());
			return 0;
		}
	}
	/**
	 * Получить ответ от срвера
	 * @return Ответ
	 */
	public String getResponse() {
		if (error) return "";
		if (!waitResponse()) {
			if (DEBUG) Log.getLogger().warning("[HTTPRequest] Ожидание ответа вернуло false.");
			return "";
		}
		return rawgetResponse();
	}
	private String rawgetResponse() {
		try {
			InputStream is = conn.getInputStream();
			StringBuilder response;
			try (BufferedReader rd = new BufferedReader(new InputStreamReader(is))) {
				response = new StringBuilder();
				String line;
				while ((line = rd.readLine()) != null) {
					response.append(line);
					if (n) response.append("\n");
				}
			}
			is.close();
			String str = response.toString();
			if (DEBUG) Log.getLogger().info("[HTTPRequest] \""+url.getHost()+"\" вывод прочитан за "+(System.currentTimeMillis()-connectedTime)+" ms");
			if (DEBUG) Log.getLogger().info("[HTTPRequest] \""+url.getHost()+"\" общее время обработки:  "+(System.currentTimeMillis()-startTime)+" ms");
			return str;
		} catch (Exception e) {
			//Log.getLogger().warning("Ошибка: Не удалось получить ответ от сервера. "+e.getMessage());
			Log.getLogger().log(Level.WARNING,"Ошибка: Не удалось получить ответ от "+url.getHost()+url.getPath()+". "+e.getMessage(),e);
			return "";
		}
	}
	/**
	 * Вывести ошибку
	 * @return Ошибка
	 */
	public String getError() {
		try {
			InputStreamReader isr;
			InputStream is = conn.getErrorStream();
			if (is == null) return null;
			if (gzip) {
				GZIPInputStream gis = new GZIPInputStream(is);
				isr = new InputStreamReader(gis);
			} else {
				isr = new InputStreamReader(is,"UTF-8");
			}
			StringBuilder response;
			try (BufferedReader rd = new BufferedReader(isr)) {
				response = new StringBuilder();
				String line;
				while ((line = rd.readLine()) != null) {
					response.append(line);
					if (n) response.append("\n");
				}
			}
			isr.close();
			String str = response.toString();
			Log.getLogger().info("[HTTPRequest] ошибка прочитана за "+(System.currentTimeMillis()-startTime));
			return str;
		} catch (Exception e) {
			//Log.getLogger().warning("Ошибка: Не удалось получить ответ от сервера. "+e.getLocalizedMessage());
			Log.getLogger().log(Level.WARNING,"Ошибка: Не удалось получить ответ от сервера. "+e.getMessage(),e);
			return "";
		}
	}
	
	public void addAction(Action action) {
		action.setHTTPRequest(this);
		actionAfterRequest = action;
	}
	public void waitRequest() {
		if (requestThread != null && requestThread.isAlive()) {
			synchronized (requestThread) {
				try {
					requestThread.wait(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					Log.getLogger().warning("[HTTPRequest] Запрос остановлен");
				}
			}
		}
	}
	
	public static abstract class Action implements Runnable {
		
		private HTTPRequest REQUEST;
		
		public Action() {}
		
		public abstract void run();
		
		public void error() {};
		
		public void setHTTPRequest(HTTPRequest request) {
			REQUEST = request;
		}
		
		protected int getResponseCode() {
			return REQUEST.rawgetResponseCode();
		}
		
		protected String getError() {
			return REQUEST.getError();
		}
		
		protected String getResponse() {
			return REQUEST.rawgetResponse();
		}
	}
	
	public static class RequestConnect implements Runnable {
		
		protected HTTPRequest PARENT;
		protected Action RUN;
		
		public RequestConnect(HTTPRequest request, Action runAfterRequest) {
			PARENT = request;
			RUN = runAfterRequest;
		};
		
		public void run() {
			try {
				if (PARENT.CHARSET.equalsIgnoreCase(""))
					PARENT.conn.setRequestProperty("Content-Type", "text/html; charset=utf-8");
				if (PARENT.gzip)
					PARENT.conn.setRequestProperty("Accept-Encoding", "gzip");
				PARENT.conn.setRequestProperty("Content-Length", Integer.toString(PARENT.postData.length));
				PARENT.conn.connect();
				if (PARENT.output) {
					DataOutputStream wr = new DataOutputStream(PARENT.conn.getOutputStream());
					wr.write(PARENT.postData);
					wr.close();
				}
				synchronized (PARENT.requestThread) {
					PARENT.connectedTime = System.currentTimeMillis();
					if (DEBUG) Log.getLogger().info("[HTTPRequest] \""+PARENT.url.getHost()+"\" запрос подключен за "+(System.currentTimeMillis()-PARENT.startTime)+" ms");
				}
				synchronized (PARENT) {
					if (RUN != null) RUN.run();
				}
			} catch (ConnectException e) {
				Log.getLogger().warning("[HTTPRequest] Ошибка: Не удалось подключиться к \""+PARENT.url.getHost()+PARENT.url.getPath()+"\". "+e.getMessage());
				PARENT.error = true;
				if (RUN != null) RUN.error();
			} catch (SocketTimeoutException e) {
				Log.getLogger().warning("[HTTPRequest] Ошибка: Не удалось подключиться к \""+PARENT.url.getHost()+PARENT.url.getPath()+"\". "+e.getMessage());
				PARENT.error = true;
				if (RUN != null) RUN.error();
			} catch (Exception e) {
				//Log.getLogger().warning("[HTTPRequest] Ошибка: Не удалось подключиться к \""+url.getHost()+"\". "+e.getMessage());
				Log.getLogger().log(Level.WARNING,"[HTTPRequest] Ошибка: Не удалось подключиться к \""+PARENT.url.getHost()+PARENT.url.getPath()+"\". "+e.getMessage(),e);
				PARENT.error = true;
				if (RUN != null) RUN.error();
			} finally {
				synchronized (PARENT.requestThread) {
					PARENT.requestThread.notifyAll();
				}
			}
		}
		
		
	}
}
