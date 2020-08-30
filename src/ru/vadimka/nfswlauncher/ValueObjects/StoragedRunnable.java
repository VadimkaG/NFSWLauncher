package ru.vadimka.nfswlauncher.ValueObjects;

/**
 * Runnable, который может хранить данные и позже может их вернуть
 * @author vadimka
 *
 * @param <Data> - Тип хранимых данных
 */
public abstract class StoragedRunnable<Data> implements Runnable {

	protected Data data;
	public StoragedRunnable() {
		data = null;
	}
	protected void setData(Data data) {
		this.data = data;
	}
	public Data getData() {
		return data;
	}

}
