package ru.vadimka.nfswlauncher.ValueObjects;

import ru.vadimka.nfswlauncher.protocol.ServerInterface;

public class ServerVO {
	private String IP;
	private String redirIP;
	private String NAME;
	private boolean HTTPS;
	private ServerInterface PROTOCOL;
	
	public ServerVO(String ip, String name, boolean isHttps) {
		IP = ip;
		NAME = name;
		redirIP = null;
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
		String ip = IP;
		if (redirIP != null) ip = redirIP;
		if (HTTPS) return "https://"+ip;
		else return "http://"+ip;
	}
	/**
	 * Получить IP сервера
	 * @return String
	 */
	public String getRedirrectedIP() {
		if (redirIP == null) return IP;
		else return redirIP;
	}
	/**
	 * Установить редиррект
	 * @param ip - куда редирректить
	 */
	public void setRedirrect(String ip) {
		redirIP = ip;
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
