package ru.vadimka.nfswlauncher.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;

import ru.vadimka.nfswlauncher.ValueObjects.Account;

public class AccountStorage {
	public AccountStorage(String filePath) {
		file = new File(filePath);
	}
	public int getCount() {
		HashMap<Integer,Long[]> header = loadHeader();
		return header.size();
	}
	protected void save(Account acc, int ID) {
		if (!file.canWrite()) return;
	}
	public Account load(int ID) {
		if (!file.exists()) return null;
		if (!file.canRead()) return null;
		HashMap<Integer,Long[]> header = loadHeader();
		if (!header.containsKey(ID)) return null;
		readData(header.get(ID));
		return null;
	}
	public Account[] loadAll() {
		if (!file.exists()) return null;
		if (!file.canRead()) return null;
		return null;
	}
}
