package ru.vadimka.nfswlauncher.theme;

import java.util.List;

import ru.vadimka.nfswlauncher.Locale;
import ru.vadimka.nfswlauncher.ValueObjects.ServerVO;

public interface GraphModule {
	/**
	 * Происходит загрузка ресурсов
	 */
	void loading();
	/**
	 * Загрузка завершена
	 */
	void loadingComplite();
	/**
	 * Авторизован ли пользователь или нет
	 * @param b
	 */
	void setLogin(boolean b);
	/**
	 * Вывести диалог ошибки
	 * @param text - Текст ошибки
	 * @param title - Заголовок окна
	 */
	void errorDialog(String text, String title);
	/**
	 * Вывести диалог информации
	 * @param text - текст информации
	 * @param title - Заголовк окна
	 */
	void infoDialog(String text, String title);
	/**
	 * Заголовок с вопросом
	 * Ответы: Да, Нет
	 * @param text - текст вопроса
	 * @param title - Заголовок окна
	 * @return
	 */
	boolean questionDialog(String text, String title);
	/**
	 * Получить путь к файлу, который выберет пользователь
	 */
	String fileSelect();
	/**
	 * Показать/Скрыть окно
	 */
	void setVisible(boolean b);
	/**
	 * Обновить список серверов
	 * @param servers - список серверов
	 */
	void updateServers(List<ServerVO> servers);
	/**
	 * Обновить локали
	 * @param locales - список локалей
	 */
	void updateLocales(Locale[] locales);
	/**
	 * уничтожить окно
	 */
	void destroy();
}
