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
		
	}

	@Override
	public void login(Account acc) throws AuthException {}

	@Override
	public void register(String login, String password) throws AuthException {
		
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
}
