package ru.vadimka.nfswlauncher.protocol;

import ru.vadimka.nfswlauncher.AuthException;
import ru.vadimka.nfswlauncher.ValueObjects.Account;

public interface ServerInterface {
	/**
	 * Получить параметр сервера
	 * @param alias - идентификатор параметра
	 */
	public String get(String alias);
	/**
	 * Авторизоваться на сервере
	 * @param acc - аккаут, который нужно авторизовать
	 * @throws AuthException - Возвращает в случае не успешной авторизации
	 */
	public void login(Account acc) throws AuthException;
	/**
	 * Зарегистрировать нового пользщователя
	 * @param login - Логин пользователя
	 * @param password - Пароль пользователя
	 * @throws AuthException - Возвращает в случае не успешной регистрации
	 */
	public void register(String login, String password) throws AuthException;
	/**
	 * Получить ссылку на восстановление пароля
	 */
	public String getLinkForgotPassword();
	/**
	 * Получить ссылку на игровые запросы сервера
	 * @return
	 */
	public String getServerEngine();
	/**
	 * Получить информацию о сервере
	 */
	public ServerInterface getResponse();
	/**
	 * Онлайн ли сервер
	 */
	public boolean isOnline();
	/**
	 * Получить имя протокола
	 */
	public String getNameProtocol();
	/**
	 * 
	 */
	public void ping();
}
