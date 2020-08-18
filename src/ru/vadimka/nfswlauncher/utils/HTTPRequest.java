package ru.vadimka.nfswlauncher.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;

import ru.vadimka.nfswlauncher.Config;
import ru.vadimka.nfswlauncher.Log;

public class HTTPRequest implements Runnable {
	
	public static final String DELIMER_FIRST = "?";
	public static final String DELIMER = "&";
	
	private static final boolean DEBUG = false;
	
	protected HttpURLConnection connection;
	protected Thread requestThread;
	
	protected URL url;
	protected String method;
	protected byte[] postData;
	
	protected Action actionAfterRequest;
	
	protected String ContentType = "text/html";
	protected String Charset = "utf-8";
	protected String UserAgent = "Mozilla/5.0 ("+System.getProperty("os.name")+" "+System.getProperty("os.arch")+") "+Config.WINDOW_TITLE+" "+Config.VERSION;
	protected Integer ConnectionTimeout = 3000;
	protected boolean ResponseEndOfLine = false;
	protected HashMap<String,String> CustomHeaders;
	public boolean DisconnectAfterPoc = true;
	
	protected String response;
	protected Integer responseCode;
	protected String error;
	
	// Если произошла ошибка во время выполнения запроса
	protected boolean ErrorAfterProc = false;
	
	/**
	 * HTTP запрос
	 * @param URL - Ссылка на запрос
	 * @param postParams - post параметры запроса
	 * @param methodPost - POST или GET запрос
	 * @param action - Действие после запроса
	 */
	public HTTPRequest(String URL, Action action, String postParams, Boolean methodPost) {
		connection = null;
		response = null;
		responseCode = null;
		requestThread = null;
		CustomHeaders = new HashMap<String,String>();
		try {
			url = new URL(URL);
		} catch (MalformedURLException e) {
			url = null;
		}
		if (methodPost) {
			method = "POST";
			ContentType = "application/x-www-form-urlencoded";
		} else
			method = "GET";
		postData = postParams.getBytes( StandardCharsets.UTF_8 );
		addAction(action);
	}
	/**
	 * HTTP запрос
	 * @param URL - Ссылка на запрос
	 * @param postParams - post параметры запроса
	 * @param methodPost - POST или GET запрос
	 */
	public HTTPRequest(String URL, String postParams, Boolean methodPost) {
		this(URL,null,postParams,methodPost);
	}
	/**
	 * POST запрос
	 * @param URL - Ссылка на запрос
	 * @param postParams - POST параметры
	 * @param action - Действие после запроса
	 */
	public HTTPRequest(String URL, Action action, String postParams) {
		this(URL, action,postParams,true);
	}
	/**
	 * GET запрос
	 * @param URL - Ссылка на запрос
	 * @param action - Действие после запроса
	 */
	public HTTPRequest(String URL, Action action) {
		this(URL, action,"",false);
	}
	/**
	 * GET запрос
	 * @param URL - Ссылка на запрос
	 */
	public HTTPRequest(String URL) {
		this(URL, null,"",false);
	}
	/**
	 * Запустить выполнение запроса
	 */
	public void proc() {
		requestThread = new Thread(this);
		requestThread.start();
	}
	/**
	 * Добавить действие после выполнения запроса
	 * @param action
	 */
	public void addAction(Action action) {
		if (action != null)
			action.setHTTPRequest(this);
		actionAfterRequest = action;
	}
	/**
	 * Установить тип контента
	 * @param type - тип контента
	 */
	public void setContentType(String type) {
		ContentType = type;
	}
	/**
	 * Установить юзер-агент
	 * @param agent
	 */
	public void setUserAgent(String agent) {
		UserAgent = agent;
	}
	/**
	 * Установить кодировку передаваемых данных
	 * По умолчанию - UTF-8
	 * @param charset - Название Кодировки
	 */
	public void setCharset(String charset) {
		Charset = charset;
	}
	/**
	 * Выводить ли \n в конце каждой строки
	 * По умолчанию false
	 * @param value
	 */
	public void setN(Boolean value) {
		ResponseEndOfLine = value;
	}
	/**
	 * Установить заголовок для запроса
	 * @param alias - Название заголовка
	 * @param value - Значение заголовка
	 */
	public void setHeader(String alias, String value) {
		if (CustomHeaders.containsKey(alias))
			CustomHeaders.replace(alias, value);
		else
			CustomHeaders.put(alias, value);
	}
	/**
	 * Подождать завершения запроса
	 * @param time - Время ожидания
	 * @return false, если запрос не успел выполнится или получил Exception
	 */
	public boolean waitResponse(int time) {
		if (requestThread == null) {
			proc();
		}
		if (requestThread.isAlive()) {
			synchronized (requestThread) {
				try {
					if (time < 1)
						requestThread.wait();
					else {
						long start_time = System.currentTimeMillis();
						requestThread.wait(time);
						if (System.currentTimeMillis() - start_time > time) {
							Log.getLogger().warning("Запрос проработал больше "+time+" секунд и не успел обработаться.");
							return false;
						}
					}
					if (ErrorAfterProc) return false;
					return true;
				} catch (InterruptedException e) {
					Log.getLogger().log(Level.WARNING,"Запрос насильно завершен",e);
					return false;
				}
			}
		}
		return true;
	}
	/**
	 * Подождать завершения запроса
	 * @return false, если запрос не успел выполнится или получил Exception
	 */
	public boolean waitResponse() {
		if (ConnectionTimeout != null)
			return waitResponse(ConnectionTimeout);
		else
			return waitResponse(0);
	}
	@Override
	public void run() {
		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setUseCaches(false);
			
			connection.setRequestProperty("Content-Type",ContentType);
			connection.setRequestProperty("Charset",Charset);
			connection.setRequestProperty("User-Agent", UserAgent);
			
			for (Entry<String, String> entry : CustomHeaders.entrySet()) {
				connection.setRequestProperty(entry.getKey(), entry.getValue());
			}
			
			if (ConnectionTimeout != null)
				connection.setConnectTimeout(ConnectionTimeout);
			
			boolean isPost = method.equalsIgnoreCase("POST");
			
			if (isPost) {
				connection.setRequestProperty("Content-Length",Integer.toString(postData.length));
				connection.setDoOutput(true);
			}
			
			long start_time = 0;
			long exec_time = 0;
			
			if (DEBUG) {
				start_time = System.currentTimeMillis();
				Log.getLogger().info("[HTTPRequest] Запускаю запрос на '"+ url.getHost()+url.getPath()+"'");
			}
			
			connection.connect();

			
			if (DEBUG)
				Log.getLogger().info("[HTTPRequest] Подключился за "+ (System.currentTimeMillis()-start_time)+ " ms.  ["+ url.getHost()+url.getPath()+"]");
			
			if (isPost) {
				try(OutputStream os = connection.getOutputStream()) {
					os.write(postData);
					os.close();
				}
			}
			

			if (DEBUG)
				Log.getLogger().info("[HTTPRequest] Время запроса без оработки данных "+ (System.currentTimeMillis()-start_time) +" ms. ["+ url.getHost()+url.getPath()+"]");
			
			if (DEBUG)
				exec_time = System.currentTimeMillis();
			
			if (actionAfterRequest != null) actionAfterRequest.run();

			if (DEBUG)
				Log.getLogger().info("[HTTPRequest] Данные обработаны за "+ (System.currentTimeMillis()-exec_time) +" ms. ["+ url.getHost()+url.getPath()+"]");
				
			
			if (DisconnectAfterPoc)
				connection.disconnect();

			if (DEBUG)
				Log.getLogger().info("[HTTPRequest] Общее время запроса "+ (System.currentTimeMillis()-start_time) +" ms. ["+ url.getHost()+url.getPath()+"]");
			
			if (requestThread != null)
				requestThread.notifyAll();
		} catch (IOException e) {
			Log.getLogger().log(Level.WARNING,"Не удалось выполнить запрос", e);
			ErrorAfterProc = true;
			actionAfterRequest.error();
		/**
		 * Это исключение может появиться из-за строки
		 * requestThread.notifyAll();
		 */
		} catch (IllegalMonitorStateException e) {}
	}
	public static abstract class Action implements Runnable {
		
		private HTTPRequest REQUEST;
		
		public Action() {}
		@Override
		public abstract void run();
		
		/**
		 * Вызывается, если во время запроса произошла ошибка
		 */
		public void error() {};
		/**
		 * Установить объект запроса
		 * @param request
		 */
		public void setHTTPRequest(HTTPRequest request) {
			REQUEST = request;
		}
		/**
		 * Получить код ответа
		 * @return int
		 */
		protected int getResponseCode() {
			if (REQUEST.responseCode == null) {
				try {
					REQUEST.responseCode = REQUEST.connection.getResponseCode();
				} catch (IOException e) {
					Log.getLogger().log(Level.WARNING,"Не удалось получить код ответа.",e);
				}
			}
			if (REQUEST.responseCode != null) {
				return REQUEST.responseCode;
			} else {
				Log.getLogger().warning("responseCode = null");
				return 0;
			}
		}
		/**
		 * Получить ошибку
		 * @return String
		 */
		protected String getError() {
			try (BufferedReader rd = new BufferedReader(new InputStreamReader(REQUEST.connection.getErrorStream()))) {
				String line;
				while ((line = rd.readLine()) != null) {
					REQUEST.error += line;
					if (REQUEST.ResponseEndOfLine) REQUEST.error += "\n";
				}
			} catch (IOException e) {
				Log.getLogger().log(Level.WARNING,"Не удалось прочитать ошибку",e);
				return "";
			}
			if (REQUEST.error != null)
				return REQUEST.error.toString();
			else {
				Log.getLogger().warning("error = null");
				return "";
			}
		}
		/**
		 * Получить ответ
		 * @return String
		 */
		protected String getResponse() {
			if (REQUEST.response == null) {
				
				long start_time = 0;
				
				if (DEBUG)
					start_time = System.currentTimeMillis();
				
				try (BufferedReader rd = new BufferedReader(new InputStreamReader(REQUEST.connection.getInputStream()))) {
					REQUEST.response = "";
					String line;
					while ((line = rd.readLine()) != null) {
						REQUEST.response += line;
						if (REQUEST.ResponseEndOfLine) REQUEST.response += "\n";
					}
					rd.close();
				} catch (IOException e) {
					Log.getLogger().log(Level.WARNING,"Не удалось прочитать ответ",e);
					return "";
				}
				
				if (DEBUG)
					Log.getLogger().info("[HTTPRequest] Данные получены за "+ (System.currentTimeMillis()-start_time) +" ms. ["+ REQUEST.url.getHost()+REQUEST.url.getPath()+"]");
			}
			if (REQUEST.response != null)
				return REQUEST.response;
			else {
				Log.getLogger().warning("response = null");
				return "";
			}
		}
	}
	public static class ActionAutoContainer extends Action {
		
		public int RESPONSE_CODE;
		public String RESPOSE;
		public String ERROR;
		
		public ActionAutoContainer() {
			RESPONSE_CODE = 0;
			RESPOSE = null;
		}
		
		@Override
		public void run() {
			RESPONSE_CODE = getResponseCode();
			if (RESPONSE_CODE == 200) {
				RESPOSE = getResponse();
			} else {
				ERROR = getError();
			}
		}
		@Override
		public String toString() {
			if (RESPOSE != null) return RESPOSE;
			else if (ERROR != null) return ERROR;
			else return "";
		}
	}
}
