package ru.vadimka.nfswlauncher.ValueObjects;

import ru.vadimka.nfswlauncher.theme.GraphActions;

public enum PerformanceLevel {
	MIN("msg_client_performance_min",0),
	LOW("msg_client_performance_low",1),
	MID("msg_client_performance_mid",2),
	HIGH("msg_client_performance_high",3),
	MAX("msg_client_performance_max",4),
	OTHER("msg_client_performance_other",5);
	
	private String TITLE;
	private int ID;
	
	private PerformanceLevel(String title, int id) {
		TITLE = title;
		ID = id;
	}
	/**
	 * Найти элемент по идентификатору
	 * @param id - идентификатор
	 */
	public static PerformanceLevel getById(int id) {
		for(PerformanceLevel v:PerformanceLevel.values())
			if (v.getID() == id) return v;
		return PerformanceLevel.OTHER;
	}
	/**
	 * Получить отображаемой имя
	 */
	public String getTitle() {
		return GraphActions.getLocale().get(TITLE);
	}
	/**
	 * Получить ID
	 */
	public int getID() {
		return ID;
	}
	@Override
	public String toString() {
		return getTitle();
	}
}
