package ru.vadimka.nfswlauncher.protocol;

import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ru.vadimka.nfswlauncher.AuthException;
import ru.vadimka.nfswlauncher.Log;
import ru.vadimka.nfswlauncher.Main;
import ru.vadimka.nfswlauncher.ValueObjects.Account;
import ru.vadimka.nfswlauncher.ValueObjects.ServerVO;
import ru.vadimka.nfswlauncher.utils.HTTPRequest;

public class Soapbox implements ServerInterface {
	
	protected HashMap<String,String> STORAGE = new HashMap<String,String>();
	
	protected ServerVO VO;
	
	protected boolean SERVER_ONLINE = false;
	
	public Soapbox(ServerVO vo) {
		VO = vo;
		if (!VO.getIP().equalsIgnoreCase("")) {
			Thread ping = new Thread(() -> {
				HttpURLConnection conn = null;
				try {
					URL url = new URL("http://"+VO.getIP()+"/soapbox-race-core/Engine.svc/GetServerInformation");
					conn = (HttpURLConnection) url.openConnection();
					conn.connect();
					if (conn.getResponseCode() == 200)
						SERVER_ONLINE = true;
					conn.disconnect();
				} catch (IOException e) {
					Log.print("Ошибка при попытке запинговать сервер "+VO.getName());
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
						Log.print("Сервер "+vo.getName()+" не отвечает.");
					}
				} catch (InterruptedException e) {}
			}
		}
	}
	
	public String getNameProtocol() {
		return "soapbox";
	}
	
	public boolean isOnline() {
		return SERVER_ONLINE;
	}
	
	@Override
	public String get(String alias) {
		if (STORAGE.containsKey(alias)) return STORAGE.get(alias);
		return alias;
	}

	@Override
	public void login(Account acc) throws AuthException {
		if (!SERVER_ONLINE) throw new AuthException("Сервер не отвечает");
		
		if (acc.getLogin().equalsIgnoreCase("") || acc.getPassword().equalsIgnoreCase("")) {
			throw new AuthException("Данные для авторизации пусты");
		}
		HTTPRequest request = new HTTPRequest(
				"http://"
				+VO.getIP()
				+"/soapbox-race-core/Engine.svc/User/authenticateUser"
				+HTTPRequest.DELIMER_FIRST
				+"email="
				+acc.getLogin()
				+HTTPRequest.DELIMER
				+"password="
				+acc.getPassword()
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
				Log.print("Ошибка логина: Не удалось разобрать данные в ответе. Ответ: "+xml);
				Log.print(e.getStackTrace());
				throw new AuthException("Не удалось разобрать ответ от сервера..."+e.getMessage());
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
				Log.print("Ошибка парсинга ответа от сервера (ошибки)");
				throw new AuthException("Ошибка при разборе полученных данных от сервера.");
			} catch (SAXException e) {
				Log.print("Ошибка синтаксиса ответа от сервера (ошибки)");
				throw new AuthException("Ошибка при разборе полученных данных от сервера.");
			} catch (IOException e) {
				Log.print("Ошибка чтения ответа от сервера (ошибки)");
				throw new AuthException("Ошибка при разборе полученных данных от сервера.");
			}
		}
	}

	@Override
	public void register(String login, String password) throws AuthException {
		if (!SERVER_ONLINE) throw new AuthException("Сервер не отвечает");
		if (login.length() < 2 || !EmailValidate(login)) {
			Log.print("Ошибка регистрации: Email введен не верно!");
			throw new AuthException(Main.locale.get("msg_reg_error_email"));
		}
		
		String URL = "http://"
				+VO.getIP()
				+"/soapbox-race-core/Engine.svc/User/createUser"
				+HTTPRequest.DELIMER_FIRST
				+"email="
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
				Log.print("Ошибка парсинга ответа от сервера (ошибки)");
				throw new AuthException("Ошибка при разборе данных с сервера");
			} catch (SAXException e) {
				Log.print("Ошибка синтаксиса ответа от сервера (ошибки)");
				throw new AuthException("Ошибка при разборе данных с сервера");
			} catch (IOException e) {
				Log.print("Ошибка чтения ответа от сервера (ошибки)");
				throw new AuthException("Ошибка при разборе данных с сервера");
			}
		}
	}

	@Override
	public String getLinkForgotPassword() {
		return "http://"+VO.getIP()+"/soapbox-race-core/forgotPasswd.jsp";
	}

	@Override
	public String getServerEngine() {
		return "http://"+VO.getIP()+"/soapbox-race-core/Engine.svc";
	}
	
	public ServerInterface getResponse() {
		if (!SERVER_ONLINE) return this;
		STORAGE.clear();
		HTTPRequest request = new HTTPRequest("http://"+VO.getIP()+"/soapbox-race-core/Engine.svc/GetServerInformation");
		request.proc();
		if (request.getResponseCode() == 200) {
			String result = request.getResponse();
			//System.out.println(result);
			try {
				Object obj = new JSONParser().parse(result);
				JSONObject jo = (JSONObject) obj;
				
				if (jo.containsKey("maxOnlinePlayers"))
					STORAGE.put("PLAYERS_MAX", String.valueOf((long) jo.get("maxOnlinePlayers")));
				else STORAGE.put("PLAYERS_MAX", "0");
				
				if (jo.containsKey("onlineNumber"))
					STORAGE.put("PLAYERS_ONLINE", String.valueOf((long) jo.get("onlineNumber")));
				else STORAGE.put("PLAYERS_ONLINE", "0");
				
				if (jo.containsKey("requireTicket"))
					STORAGE.put("INVITE_REQUIRED", String.valueOf((boolean) jo.get("requireTicket")));
				else 
					STORAGE.put("INVITE_REQUIRED", "false");
				
				if (jo.containsKey("serverName")) {
					String name = (String) jo.get("serverName");
					if (name.equalsIgnoreCase(""))
						STORAGE.put("SERVER_NAME", VO.getName());
					else
						STORAGE.put("SERVER_NAME", (String) jo.get("serverName"));
				}
				
				if (jo.containsKey("discordUrl"))
					STORAGE.put("DISCORD", (String) jo.get("discordUrl"));
				else STORAGE.put("DISCORD", "");
				
				if (jo.containsKey("homePageUrl"))
					STORAGE.put("WEB_SITE", (String) jo.get("homePageUrl"));
				else STORAGE.put("WEB_SITE", "");
				
				if (jo.containsKey("messageSrv"))
					STORAGE.put("DESCRIPRION", (String) jo.get("messageSrv"));
				else STORAGE.put("DESCRIPRION", "");

				if (jo.containsKey("bannerUrl"))
					STORAGE.put("BANNER", (String) jo.get("bannerUrl"));
				else STORAGE.put("BANNER", "");
				
			} catch (ParseException e) {
				System.out.println("Ошибка: Не удалось разобрать данные пришедшие с "+VO.getIP()+". "+e.getMessage());
				System.out.println("Пришедшие данные: "+result);
			} catch (Exception e) {
				Log.print("Ошибка при разборе данных "+VO.getIP()+". "+e.getLocalizedMessage());
				Log.print(e.getStackTrace());
			}
		}
		return this;
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

}
