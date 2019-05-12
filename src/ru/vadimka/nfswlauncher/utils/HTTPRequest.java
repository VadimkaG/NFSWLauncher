package ru.vadimka.nfswlauncher.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
	
	/**
	 * HTTP запрос
	 * @param URL - Ссылка на страницу
	 * @param params - параметры запроса
	 * @param methodPost - Если true, то запрос POST. Если false, запрос GET
	 */
	public HTTPRequest(String URL,String params, Boolean methodPost) {
		n = false;
		gzip = false;
		try {
			url = new URL(URL);
			postData = params.getBytes( StandardCharsets.UTF_8 );
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			output = true;
			conn.setDoOutput(output);
			conn.setUseCaches(false);
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 ("+System.getProperty("os.name")+" "+System.getProperty("os.arch")+") "+Config.WINDOW_TITLE+" "+Config.VERSION);
			if (methodPost) {
				conn.setRequestMethod("POST");
			} else {
				conn.setRequestMethod("GET");
			}
			CHARSET = "";
		} catch (MalformedURLException e) {
			Log.print("Ошибка: Не корректная ссылка: "+URL);
			Log.print(e.getStackTrace());
		} catch (IOException e) {
			Log.print("Ошибка: Не удалось создать соединение с "+URL+". "+e.getMessage());
		} catch (Exception e) {
			Log.print("Ошибка: Непредвиденная ошибка инициализации соединения с "+URL);
			Log.print(e.getStackTrace());
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
			conn.setDoInput(true);
			output = false;
			conn.setDoOutput(output);
			conn.setUseCaches(false);
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 ("+System.getProperty("os.name")+" "+System.getProperty("os.arch")+") "+Config.WINDOW_TITLE+" "+Config.VERSION);
			conn.setRequestMethod("GET");
			CHARSET = "";
		} catch (MalformedURLException e) {
			Log.print("Ошибка: Не корректная ссылка: "+URL);
			Log.print(e.getStackTrace());
		} catch (IOException e) {
			Log.print("Ошибка: Не удалось создать соединение с "+URL+". "+e.getMessage());
		} catch (Exception e) {
			Log.print("Ошибка: Непредвиденная ошибка инициализации соединения с "+URL);
			Log.print(e.getStackTrace());
		}
	}
	/**
	 * Установить кодировку передаваемых данных
	 * По умолчанию - UTF-8
	 * @param charset - Название Кодировки
	 */
	public void setCharset(String charset) {
		CHARSET = charset;
		conn.setRequestProperty("charset", charset);
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
		try {
			if (CHARSET.equalsIgnoreCase(""))
				conn.setRequestProperty("charset", "utf-8");
			if (gzip)
				conn.setRequestProperty("Accept-Encoding", "gzip");
			conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
			conn.connect();
			if (output) {
				DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
				wr.write(postData);
				wr.close();
			}
		} catch (Exception e) {
			Log.print("Ошибка: Не удалось подключиться к серверу. "+e.getMessage());
		}
		
	}
	/**
	 * Получить код ответа от сервера
	 * @return - Код ответа
	 */
	public int getResponseCode() {
		try {
			return conn.getResponseCode();
		} catch (IOException e) {
			Log.print("Ошибка: Не удалось получить код ответа от сервера. "+e.getMessage());
			return 0;
		} catch (Exception e) {
			Log.print("Ошибка: Непредвиденная ошибка при получении кода ответа от "+conn.getURL().toString());
			Log.print(e.getStackTrace());
			return 0;
		}
	}
	/**
	 * Получить ответ от срвера
	 * @return Ответ
	 */
	public String getResponse() {
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
			return str;
		} catch (Exception e) {
			Log.print("Ошибка: Не удалось получить ответ от сервера. "+e.getMessage());
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
			if (gzip) {
				InputStream is = conn.getErrorStream();
				GZIPInputStream gis = new GZIPInputStream(is);
				isr = new InputStreamReader(gis);
			} else {
				InputStream is = conn.getErrorStream();
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
			Log.print(conn.getHeaderField("Content-Type"));
			return str;
		} catch (Exception e) {
			Log.print("Ошибка: Не удалось получить ответ от сервера. "+e.getLocalizedMessage());
			return "";
		}
	}
}
