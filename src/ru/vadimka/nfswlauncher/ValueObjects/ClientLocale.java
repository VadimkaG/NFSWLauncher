package ru.vadimka.nfswlauncher.ValueObjects;

public enum ClientLocale {
	Russian("RU","Русский"),
	English("EN","English"),
	German("GB","German"),
	French("FR","French"),
	Spanish("ES","Spanish"),
	Polish("PL","Polish"),
	Brazilian("BR","Brazilian"),
	General("GN","General");
	
	private String ID;
	private String TITLE;
	
	private ClientLocale(String id, String title) {
		ID = id;
		TITLE = title;
	}
	/**
	 * Получить объект по id
	 * @param id
	 * @return
	 */
	public static ClientLocale getById(String id) {
		for(ClientLocale l:ClientLocale.values()) {
			if (l.getId().toLowerCase().equalsIgnoreCase(id.trim().toLowerCase())) return l;
		}
		return ClientLocale.English;
	}
	/**
	 * Получить отображаемое имя
	 */
	public String getTitle() {
		return TITLE;
	}
	/**
	 * Получить ID
	 */
	public String getId() {
		return ID;
	}
	@Override
	public String toString() {
		return getTitle();
	}
}
