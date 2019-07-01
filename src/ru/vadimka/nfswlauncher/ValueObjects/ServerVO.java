package ru.vadimka.nfswlauncher.ValueObjects;

import ru.vadimka.nfswlauncher.protocol.ServerInterface;

public class ServerVO {
	private String IP;
	private String redirIP;
	private String NAME;
	private ServerInterface PROTOCOL;
	
	public ServerVO(String ip, String name) {
		IP = ip;
		NAME = name;
		redirIP = null;
	}
	/**
	 * Получить IP сервера
	 * @return String
	 */
	public String getIP() {
		return IP;
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
