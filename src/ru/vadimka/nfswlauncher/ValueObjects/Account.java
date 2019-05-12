package ru.vadimka.nfswlauncher.ValueObjects;

public class Account {
	private boolean IS_AUTH;
	private String ID;
	private String TOKEN;
	private ServerVO SERVER;
	private String LOGIN;
	private String PASSWORD;
	
	public Account(ServerVO srv, String login, String password) {
		SERVER = srv;
		LOGIN = login;
		PASSWORD = password;
		IS_AUTH = false;
	}
	/**
	 * Авторизовать пользователя
	 * @param id - Индентификатор пользователя
	 * @param token - сессия пользователя
	 */
	public void login(String id, String token) {
		IS_AUTH = true;
		ID = id;
		TOKEN = token;
	}
	/**
	 * Деавторизоваться
	 */
	public void logout() {
		IS_AUTH = false;
	}
	/**
	 * Авторизован ли пользователь
	 * @return
	 */
	public boolean isAuth() {
		return IS_AUTH;
	}
	/**
	 * Получить логин
	 * @return
	 */
	public String getLogin() {
		return LOGIN;
	}
	/**
	 * Получить сервер, на котором пользователь авторизован
	 * @return
	 */
	public ServerVO getServer() {
		return SERVER;
	}
	/**
	 * Получить id
	 * Если пользователь не авторизован, то вернет пустую строку
	 * @return
	 */
	public String getID() {
		if (IS_AUTH) return ID;
		else return "";
	}
	/**
	 * Получить токен
	 * Если пользователь не авторизован, то вернет пустую строку
	 * @return
	 */
	public String getToken() {
		if (IS_AUTH) return TOKEN;
		else return "";
	}
	/**
	 * Получить пароль
	 * @return
	 */
	public String getPassword() {
		return PASSWORD;
	}
	
	@Override
	public String toString() {
		return SERVER.toString()+" : "+LOGIN;
	}
}
