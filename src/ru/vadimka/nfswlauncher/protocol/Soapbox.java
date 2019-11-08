package ru.vadimka.nfswlauncher.protocol;

import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;
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
import ru.vadimka.nfswlauncher.Config;
import ru.vadimka.nfswlauncher.Log;
import ru.vadimka.nfswlauncher.Main;
import ru.vadimka.nfswlauncher.ValueObjects.Account;
import ru.vadimka.nfswlauncher.ValueObjects.RWACIndex;
import ru.vadimka.nfswlauncher.ValueObjects.ServerVO;
import ru.vadimka.nfswlauncher.client.Game;
import ru.vadimka.nfswlauncher.client.GameStartException;
import ru.vadimka.nfswlauncher.utils.HTTPRequest;
import ru.vadimka.nfswlauncher.utils.HTTPRequest.Action;
import ru.vadimka.nfswlauncher.utils.RWAC;

public class Soapbox implements ServerInterface {
	
	protected HashMap<String,String> STORAGE = new HashMap<String,String>();
	
	protected ServerVO VO;
	
	protected boolean SERVER_ONLINE = false;
	
	protected RWACIndex RWACindex = null;
	
	private long PING = 0;
	
	public Soapbox(ServerVO vo) {
		VO = vo;
		//ping();
	}
	public void ping() {
		if (!VO.getIP().equalsIgnoreCase("")) {
			Thread ping = new Thread(() -> {
				
				HttpURLConnection conn = null;
				try {
					URL url = new URL(VO.getHttpLink()+"/");
					conn = (HttpURLConnection) url.openConnection();
					conn.setReadTimeout(1400);
					final long StartTime = System.currentTimeMillis();
					conn.connect();
					PING = System.currentTimeMillis()-StartTime;
					if (conn.getResponseCode() == 200)
						SERVER_ONLINE = true;
					else
						SERVER_ONLINE = false;
					//else Log.getLogger().info("["+VO.getName()+"]Отвечен код: "+conn.getResponseCode());
					conn.disconnect();
					
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
						PING = 0;
						SERVER_ONLINE = false;
					}
				} catch (InterruptedException e) {}
			}
		}
	}
	
	public String getNameProtocol() {
		return "soapbox";
	}
	@Override
	public boolean isOnline() {
		return SERVER_ONLINE;
	}
	
	@Override
	public String get(String alias) {
		if (STORAGE.containsKey(alias)) return STORAGE.get(alias);
		else if (alias.equalsIgnoreCase("SERVER_NAME")) return VO.getName();
		else if (alias.equalsIgnoreCase("ping")) return String.valueOf(PING);
		else if (!SERVER_ONLINE) return "";
		return alias;
	}

	@Override
	public void login(Account acc) throws AuthException {
		//if (!SERVER_ONLINE) throw new AuthException("Сервер не отвечает");
		if (acc == null) return;
		
		if (acc.getLogin().equalsIgnoreCase("") || acc.getPassword().equalsIgnoreCase("")) {
			throw new AuthException("Данные для авторизации пусты");
		}
		HTTPRequest request = new HTTPRequest(
				VO.getHttpLink()
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
			if (xml.equalsIgnoreCase("")) {
				Log.getLogger().warning("Ошибка логина: Данные пришедшие от сервера пусты....");
				throw new AuthException("Не удалось разобрать ответ от сервера... Данные пришедшие от сервера пусты");
			}
			DocumentBuilderFactory dcFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dcBuilder;
			try {
				dcBuilder = dcFactory.newDocumentBuilder();
				Document doc = dcBuilder.parse(new InputSource(new StringReader(xml)));
				acc.login(doc.getElementsByTagName("UserId").item(0).getTextContent(), doc.getElementsByTagName("LoginToken").item(0).getTextContent());
			} catch (Exception e) {
				Log.getLogger().warning("Ошибка логина: Не удалось разобрать данные в ответе. Ответ: "+xml);
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
		
		String URL = VO.getHttpLink()
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
		return VO.getHttpLink()+"/soapbox-race-core/forgotPasswd.jsp";
	}

	@Override
	public String getServerEngine() {
		return VO.getRedirrectedHttpLink()+"/soapbox-race-core/Engine.svc";
	}
	public ServerInterface getResponse() {
		return getResponse(null);
	}
	public ServerInterface getResponse(Runnable doAfter) {
		//if (!SERVER_ONLINE) return this;
		STORAGE.clear();
		HTTPRequest request = new HTTPRequest(VO.getHttpLink()+"/soapbox-race-core/Engine.svc/GetServerInformation");
		
		request.addAction(new HTTPRequest.Action() {
			@Override
			public void run() {
				if (getResponseCode() == 200) {
					SERVER_ONLINE = true;
					String result = getResponse();
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
							if (!name.equalsIgnoreCase(""))
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
						
						// Получаем ключ, необходимый для авторизации
						if (jo.containsKey("ServerPass"))
							STORAGE.put("ssp", (String) jo.get("ServerPass"));
						
						if (jo.containsKey("rwacallow") && ((boolean)jo.get("rwacallow")) == true) {
							RWACindex = new RWACIndex(
//								"https://www.dropbox.com/s/q0enm212ux5bdc8/FilesChecker.xml?dl=1"
								VO.getHttpLink()+"/soapbox-race-core/fileschecker"
									);
							RWACindex.download();
						}
						
					} catch (ParseException e) {
						Log.getLogger().warning("Ошибка: Не удалось разобрать данные пришедшие с "+VO.getIP()+". "+e.getMessage()+"\nПришедшие данные: "+result);
					} catch (Exception e) {
						Log.getLogger().log(Level.WARNING,"Ошибка при разборе данных "+VO.getIP()+". "+e.getLocalizedMessage(),e);
					}
				}
				if (doAfter != null) doAfter.run();
			}
			
			@Override
			public void error() {
				if (doAfter != null && doAfter instanceof Action)
					((Action) doAfter).error();
			}
		});

		request.proc();
		
		request.waitRequest();
		
		return this;
	}
	/**
	 * Проверить корректность ввода email
	 * @param email - Строка, которая будет проверена
	 * @return Результат проверки
	 */
	protected static Boolean EmailValidate(String email) {
		Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}
	
	public RWACIndex getRWACindex() {
		return RWACindex;
	}

//	@Override
//	public boolean useRWAC() {
//		return RWACuse;
//	}
//	
//	@Override
//	public String getRWACLink() {
//		return VO.getHttpLink()+"/soapbox-race-core/fileschecker";
//	}
//
//	@Override
//	public void setRWACindex(byte[] index) {
//		RWACindex = index;
//	}

	@Override
	public void launchGame() throws GameStartException {
		if (!RWAC.checkBeforeStart()) throw new GameStartException(Main.locale.get("error_game_is_modified"));
		//ServerRedirrect sr = new ServerRedirrect(Main.account.getServer().getIP(), 8080);
		Main.account.getServer().setRedirrect(Main.account.getServer().getIP()/*sr.getLocalHost()*/);
		Main.game = new Game(Main.account.getToken(), Main.account.getID(), Main.account.getServer().getProtocol().getServerEngine(), Config.GAME_PATH);
		//Main.frame.loadingComplite();
	}

}
