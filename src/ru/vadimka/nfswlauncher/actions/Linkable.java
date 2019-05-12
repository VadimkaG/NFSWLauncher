package ru.vadimka.nfswlauncher.actions;

public interface Linkable {
	Linkable link(String str, Getter<String> g);
}
