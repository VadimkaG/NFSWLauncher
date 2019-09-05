package ru.vadimka.nfswlauncher.protocol;

import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ru.vadimka.nfswlauncher.AuthException;
import ru.vadimka.nfswlauncher.Config;
import ru.vadimka.nfswlauncher.Log;
import ru.vadimka.nfswlauncher.Main;
import ru.vadimka.nfswlauncher.ValueObjects.Account;
import ru.vadimka.nfswlauncher.ValueObjects.ServerVO;
import ru.vadimka.nfswlauncher.client.Game;
import ru.vadimka.nfswlauncher.client.GameStartException;
import ru.vadimka.nfswlauncher.utils.HTTPRequest;

public class RacingWorld implements ServerInterface {
	
	private ServerVO VO;
	private boolean SERVER_ONLINE = false;
	private HashMap<String,String> STORAGE = new HashMap<String,String>();
	
	private long PING;
	
	public RacingWorld(ServerVO vo) {
		VO = vo;
		ping();
	}
	
	public void ping() {
		if (!VO.getIP().equalsIgnoreCase("")) {
			Thread ping = new Thread(() -> {
				HttpURLConnection conn = null;
				try {
					URL url = new URL("http://"+VO.getIP()+"/");
					conn = (HttpURLConnection) url.openConnection();
					final long StartTime = System.currentTimeMillis();
					conn.connect();
					final long EndTime = System.currentTimeMillis();
					if (conn.getResponseCode() == 200) SERVER_ONLINE = true;
					conn.disconnect();
					PING = EndTime-StartTime;
				} catch (IOException e) {
					SERVER_ONLINE = false;
					Log.getLogger().warning("Ошибка при попытке запинговать сервер "+VO.getName());
					if (conn != null)
						conn.disconnect();
				}
				
				synchronized (this) {
					notify();
				}
			});
			ping.start();
			synchronized (ping) {
				try {
					ping.wait(1300);
					if (ping.isAlive()) {
						ping.interrupt();
						//Log.getLogger().warning("Сервер "+VO.getName()+" не отвечает.");
						SERVER_ONLINE = false;
					}
				} catch (InterruptedException e) {}
			}
		}
	}
	
	@Override
	public String get(String alias) {
		if (alias == "ping") return String.valueOf(PING);
		if (!STORAGE.containsKey(alias)) return "";
		return STORAGE.get(alias);
	}

	@Override
	public void login(Account acc) throws AuthException {
		if (!SERVER_ONLINE) throw new AuthException("Сервер не отвечает");
		
		if (acc.getLogin().equalsIgnoreCase("") || acc.getPassword().equalsIgnoreCase("")) {
			throw new AuthException("Данные для авторизации пусты");
		}
		String key = "";
		
		/*
		 * Сервер обязан отдать некий зашифрованный ключ.
		 * Ключ получается при приеме информации сервера в "ServerPass"
		 * В данном случае эти данные напрямую записаны в STORAGE
		 */
		if (STORAGE.containsKey("ssp") && !STORAGE.get("ssp").equalsIgnoreCase("")) {
			// Декодируем ключ
			String str = "";
			try {
				str = new String(Base64.getDecoder().decode(STORAGE.get("ssp").getBytes()));
			} catch (IllegalArgumentException ex) {
				Log.getLogger().warning("Ошибка декодирования ключа сервера: "+ex.getMessage());
			}
			/*
			 * Разбираем данные
			 * В декодированной строке должно быть две части, разделенные $
			 * Наш ключ находится во второй части
			 */
			String[] strs = str.split("\\$");
			if (strs.length == 2)
				key = strs[1];
		}
		HTTPRequest request = new HTTPRequest(
				"http://"
				+VO.getIP()
				+"/auth"
				+HTTPRequest.DELIMER_FIRST
				+"login="
				+acc.getLogin()
				+HTTPRequest.DELIMER
				+"password="
				/*
				 * В пароль мы должны зашифровать наш ключ.
				 * А для этого создаем строку из 3-х частей
				 * В первой должен быть наш расшифрованных ключ
				 * Во вторую часть забъем какой-нибудь мусор, для отвода глаз
				 * И наконец третья часть это наш пароль
				 * Все это шифруем, а то слишком легко догадаться
				 */
				+new String(Base64.getEncoder().encode((key+"$"+String.valueOf((System.currentTimeMillis() / 1000L)/2)+"$"+acc.getPassword()).getBytes()))
			);
		request.proc();
		if (request.getResponseCode() == 200) {
			String xml = request.getResponse();
			DocumentBuilderFactory dcFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dcBuilder;
			try {
				dcBuilder = dcFactory.newDocumentBuilder();
				Document doc = dcBuilder.parse(new InputSource(new StringReader(xml)));
				acc.login(doc.getElementsByTagName("UserId").item(0).getTextContent(), doc.getElementsByTagName("LoginToken").item(0).getTextContent());
			} catch (Exception e) {
				Log.getLogger().warning("Ошибка логина: Не удалось разобрать данные в ответе. Ответ: "+xml);
				throw new AuthException("Не удалось разобрать ответ от сервера...");
			}
		} else {
			try {
				String xml = request.getError();
				InputSource source = new InputSource(new StringReader(xml));
				DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document document = documentBuilder.parse(source);
				Node Root = document.getDocumentElement();
				NodeList items = Root.getChildNodes();
				for (int i = 0; i < items.getLength(); i++) {
					Node item = items.item(i);
					if (item.getNodeName().equalsIgnoreCase("description")) {
						throw new AuthException(item.getTextContent());
					}
				}
			} catch (ParserConfigurationException e) {
				Log.getLogger().warning("Ошибка парсинга ответа от сервера (ошибки)");
				throw new AuthException("Ошибка при разборе полученных данных от сервера.");
			} catch (SAXException e) {
				Log.getLogger().warning("Ошибка синтаксиса ответа от сервера (ошибки)");
				throw new AuthException("Ошибка при разборе полученных данных от сервера.");
			} catch (IOException e) {
				Log.getLogger().warning("Ошибка чтения ответа от сервера (ошибки)");
				throw new AuthException("Ошибка при разборе полученных данных от сервера.");
			}
		}
	}

	@Override
	public void register(String login, String password) throws AuthException {
		if (!SERVER_ONLINE) throw new AuthException("Сервер не отвечает");
		if (login.length() < 2 || !EmailValidate(login)) {
			Log.getLogger().warning("Ошибка регистрации: Email введен не верно!");
			throw new AuthException(Main.locale.get("msg_reg_error_email"));
		}
		
		String URL = "http://"
				+VO.getIP()
				+"/register"
				+HTTPRequest.DELIMER_FIRST
				+"login="
				+login
				+HTTPRequest.DELIMER
				+"password="
				+password;
		HTTPRequest request = new HTTPRequest(URL);
		request.proc();
		if (request.getResponseCode() != 200) {
			try {
				String xml = request.getError();
				InputSource source = new InputSource(new StringReader(xml));
				DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document document = documentBuilder.parse(source);
				Node Root = document.getDocumentElement();
				NodeList items = Root.getChildNodes();
				for (int i = 0; i < items.getLength(); i++) {
					Node item = items.item(i);
					if (item.getNodeName().equalsIgnoreCase("description")) {
						throw new AuthException(item.getTextContent());
					}
				}
			} catch (ParserConfigurationException e) {
				Log.getLogger().warning("Ошибка парсинга ответа от сервера (ошибки)");
				throw new AuthException("Ошибка при разборе данных с сервера");
			} catch (SAXException e) {
				Log.getLogger().warning("Ошибка синтаксиса ответа от сервера (ошибки)");
				throw new AuthException("Ошибка при разборе данных с сервера");
			} catch (IOException e) {
				Log.getLogger().warning("Ошибка чтения ответа от сервера (ошибки)");
				throw new AuthException("Ошибка при разборе данных с сервера");
			}
		}
	}

	@Override
	public String getLinkForgotPassword() {
		return "http://"+VO.getIP()+"forgot_password";
	}

	@Override
	public String getServerEngine() {
		return "http://"+VO.getIP()+"/Engine.svc";
	}

	@Override
	public ServerInterface getResponse() {
		if (!SERVER_ONLINE) return this;
		STORAGE.clear();
		HTTPRequest request = new HTTPRequest("http://"+VO.getIP()+"/server_info");
		request.proc();
		if (request.getResponseCode() == 200) {
			STORAGE.clear();
			try {
				DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
				Document doc = documentBuilder.parse(new InputSource(new StringReader(request.getResponse())));
				Node el = doc.getElementsByTagName("ServerName").item(0);
				if (el != null && !el.getTextContent().equalsIgnoreCase(""))
					STORAGE.put("SERVER_NAME", el.getTextContent());
				else
					STORAGE.put("SERVER_NAME", VO.getName());
				
				el = doc.getElementsByTagName("Description").item(0);
				if (el != null && !el.getTextContent().equalsIgnoreCase(""))
					STORAGE.put("DESCRIPRION", el.getTextContent());
				else
					STORAGE.put("DESCRIPRION", VO.getName());
				
				el = doc.getElementsByTagName("Description").item(0);
				if (el != null && !el.getTextContent().equalsIgnoreCase(""))
					STORAGE.put("DESCRIPRION", el.getTextContent());
				else
					STORAGE.put("DESCRIPRION", VO.getName());
				
				el = doc.getElementsByTagName("WebSite").item(0);
				if (el != null && !el.getTextContent().equalsIgnoreCase(""))
					STORAGE.put("WEB_SITE", el.getTextContent());
				else
					STORAGE.put("WEB_SITE", VO.getName());
				
				el = doc.getElementsByTagName("PlayersOnline").item(0);
				if (el != null && !el.getTextContent().equalsIgnoreCase(""))
					STORAGE.put("PLAYERS_ONLINE", el.getTextContent());
				else
					STORAGE.put("PLAYERS_ONLINE", VO.getName());
				
				el = doc.getElementsByTagName("PlayersMax").item(0);
				if (el != null && !el.getTextContent().equalsIgnoreCase(""))
					STORAGE.put("PLAYERS_MAX", el.getTextContent());
				else
					STORAGE.put("PLAYERS_MAX", VO.getName());
				
				el = doc.getElementsByTagName("Garbage").item(0);
				if (el != null && !el.getTextContent().equalsIgnoreCase(""))
					STORAGE.put("ssp", el.getTextContent());
				else
					STORAGE.put("ssp", VO.getName());
			} catch (ParserConfigurationException e) {
				Log.getLogger().warning("Ошибка при разборе информации о сервере "+VO.getName());
			} catch (SAXException e) {
				Log.getLogger().warning("Ошибка синтаксиса при разборе информации о сервере "+VO.getName());
			} catch (IOException e) {
				Log.getLogger().log(Level.WARNING,"Ошибка ввода/вывода "+VO.getName()+": "+e.getMessage(),e);
			}
		}
		return this;
	}

	@Override
	public boolean isOnline() {
		return SERVER_ONLINE;
	}

	@Override
	public String getNameProtocol() {
		return "RacingWorld";
	}
	/**
	 * Проверить корректность ввода email
	 * @param email - Строка, которая будет проверена
	 * @return Результат проверки
	 */
	private static Boolean EmailValidate(String email) {
		Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	@Override
	public byte[] getRWACindex() {
		return null;
	}

	@Override
	public void launchGame() throws GameStartException {
		Main.account.getServer().setRedirrect(Main.account.getServer().getIP());
		Main.game = Game.call(Main.account.getToken(), Main.account.getID(), Main.account.getServer().getProtocol().getServerEngine(), Config.GAME_PATH);
	}

	@Override
	public ServerInterface getResponse(Runnable doAfter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean useRWAC() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setRWACindex(byte[] index) {
		// TODO Auto-generated method stub
		
	}
}
