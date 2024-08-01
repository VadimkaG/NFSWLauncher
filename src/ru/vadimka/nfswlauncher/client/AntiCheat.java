package ru.vadimka.nfswlauncher.client;

public interface AntiCheat {
	boolean checkBeforeStart();
	void download();
	void renameProcess(final String title, final long handle);
	boolean checkRepitedly();
}
