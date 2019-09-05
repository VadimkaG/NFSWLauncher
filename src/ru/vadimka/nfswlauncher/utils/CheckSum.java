package ru.vadimka.nfswlauncher.utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import ru.vadimka.nfswlauncher.Log;

public abstract class CheckSum {
	/**
	 * Получить контрольную сумму файла
	 * @param path - Путь к файлу
	 * @return контрольная сума или byte[0] в случае ошибки
	 */
	public static byte[] get(InputStream fis) {
		if (fis == null) {
			Log.getLogger().warning("[Checksum] Не обнаружен входящий поток");
			return new byte[0];
		}
		try {
			//InputStream fis = new FileInputStream(path);
			MessageDigest complete = MessageDigest.getInstance("MD5");
			int readed = 0;
			byte[] buffer = new byte[1024];
			while ((readed = fis.read(buffer)) != -1) {
				if (readed > 0)
					complete.update(buffer, 0, readed);
			}
			fis.close();
			return complete.digest();
		} catch (IOException e) {
			Log.getLogger().warning("Не удалось прочитать файл.");
		} catch (NoSuchAlgorithmException e) {
			Log.getLogger().warning("[CheckSum]"+e.getMessage());
			//Log.getLogger().warning(e.getStackTrace());
		}
		return new byte[0];
	}
	/**
	 * Отдать контрольную сумму
	 * @param path - InputStream файла
	 * @param raw - Если true, то будет выводиться полностью без лишних символов
	 */
	public static String print(InputStream path, boolean raw) {
		byte[] bytes = get(path);
		return bytesToHexString(bytes, raw);
	}
	/**
	 * Преобразовать массив байтов в строку hex
	 * @param bytes - Массив байтов
	 * @param raw - Если true, то будет выводиться только два символа
	 * @return
	 */
	public static String bytesToHexString(byte[] bytes, boolean raw) {
		String data = "";
		if (bytes.length < 1) return data;
		for (int i = 0; i < bytes.length; i++) {
			if (i != 0 && !raw) data += ", ";
			if (!raw) data += "(byte) 0x";
			data += Integer.toHexString((bytes[i] >> 4) & 0xf)+""+Integer.toHexString(bytes[i] & 0xf);
		}
		return data;
	}
}
