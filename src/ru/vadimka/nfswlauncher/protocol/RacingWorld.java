package ru.vadimka.nfswlauncher.protocol;

import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
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
	
	public RacingWorld(ServerVO vo) {}
	
	@Override
	public String get(String alias) {return "";}

	@Override
	public void login(Account acc) throws AuthException {}

	@Override
	public void register(String login, String password) throws AuthException {}

	@Override
	public String getLinkForgotPassword() {return "";}

	@Override
	public String getServerEngine() {return "";}

	@Override
	public ServerInterface getResponse() {return this;}

	@Override
	public boolean isOnline() {return false;}

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
