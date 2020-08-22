package ru.vadimka.nfswlauncher.ValueObjects;

import ru.vadimka.nfswlauncher.protocol.ServerInterface;
import ru.vadimka.nfswlauncher.rserver.RedirectServer;

public class ServerVO {
	private String IP;
	private RedirectServer redirServer;
	private String NAME;
	private boolean HTTPS;
	private ServerInterface PROTOCOL;
	
	public ServerVO(String ip, String name, boolean isHttps) {
		IP = ip;
		NAME = name;
		redirServer = null;
		HTTPS = isHttps;
	}
	/**
	 * Получить IP сервера
	 * @return String
	 */
	public String getIP() {
		return IP;
	}
	/**
	 * Использует ли сервер https протокол
	 * @return true = https false = http
	 */
	public boolean isHttps() {
		return HTTPS;
	}
	/**
	 * Получить IP вместе с HTTP
	 */
	public String getHttpLink() {
		if (HTTPS) return "https://"+getIP();
		else return "http://"+getIP();
	}
	/**
	 * Получить редирект IP вместе с HTTP
	 */
	public String getRedirrectedHttpLink() {
		if (HTTPS) return "https://"+getRedirrectedIP();
		else return "http://"+getRedirrectedIP();
	}
	/**
	 * Получить IP сервера
	 * @return String
	 */
	public String getRedirrectedIP() {
		if (redirServer == null) return getIP();
		else return redirServer.getIP();
	}
	/**
	 * Установить редиррект
	 * @param ip - куда редирректить
	 */
	public void setRedirrect(int port) {
		redirServer = new RedirectServer(port,getIP());
	}
	public RedirectServer getRedirectServer() {
		return redirServer;
	}
	/**
	 * Получить название сервера
	 * @return String
	 */
	public String getName() {
		return NAME;
	}
	/**
	 * Установить протокол сервера
	 * @param server ServerInterface
	 */
	public void setProtocol(ServerInterface server) {
		PROTOCOL = server;
	}
	/**
	 * Получить серверный протокол
	 * @return ServerInterface
	 */
	public ServerInterface getProtocol() {
		return PROTOCOL;
	}
	
	@Override
	public String toString() {
		return NAME;
	}
}
