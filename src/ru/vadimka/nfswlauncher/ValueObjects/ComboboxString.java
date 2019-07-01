package ru.vadimka.nfswlauncher.ValueObjects;

public class ComboboxString<T> {
	private String NAME;
	private T VALUE;
	public ComboboxString(String name, T value) {
		NAME = name;
		VALUE = value;
	}
	/**
	 * Получить название
	 */
	public String getName() {
		return NAME;
	}
	/**
	 * Получить значение
	 */
	public T getValue() {
		return VALUE;
	}
	@Override
	public String toString() {
		return getName();
	}
}
