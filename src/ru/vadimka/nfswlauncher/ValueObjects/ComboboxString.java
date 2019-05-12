package ru.vadimka.nfswlauncher.ValueObjects;

public class ComboboxString<T> {
	private String NAME;
	private T VALUE;
	public ComboboxString(String name, T value) {
		NAME = name;
		VALUE = value;
	}
	public T getValue() {
		return VALUE;
	}
	@Override
	public String toString() {
		return NAME;
	}
}
