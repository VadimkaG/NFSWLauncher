package ru.vadimka.nfswlauncher.protocol;

import java.io.IOException;
import java.io.StringReader;
import java.util.Base64;

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
import ru.vadimka.nfswlauncher.ValueObjects.Account;
import ru.vadimka.nfswlauncher.ValueObjects.ServerVO;
import ru.vadimka.nfswlauncher.utils.HTTPRequest;

public class SoapboxLocked extends Soapbox {

	public SoapboxLocked(ServerVO vo) {
		super(vo);
	}
	
	@Override
	public String getNameProtocol() {
		return "soapbox-Locked";
	}
	/**
	 * Скрытый механизм авторизации
	 * Авторизация проходит таким образом, чтобы сервер понял, что это именно тот лаунчер, который нужен
	**/
	@Override
	public void login(Account acc) throws AuthException {}

}
