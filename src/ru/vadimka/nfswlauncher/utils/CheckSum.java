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
			Log.print("[Checksum] Не обнаружен входящий поток");
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
			Log.print("Не удалось прочитать файл.");
		} catch (NoSuchAlgorithmException e) {
			Log.print(e.getMessage());
			Log.print(e.getStackTrace());
		}
		return new byte[0];
	}
	/**
	 * Отдать контрольную сумму
	 * @param path - InputStream файла
	 * @return строку байт файла
	 * 
	 */
	public static String print(InputStream path) {
		byte[] b = {};
		b = get(path);
		int len = b.length;
		if (len < 1) return "";
		String data = new String();
		for (int i = 0; i < len; i++) {
			if (i != 0) data += ", ";
			data += "(byte) 0x"+Integer.toHexString((b[i] >> 4) & 0xf)+""+Integer.toHexString(b[i] & 0xf);
		}
		return data;
	}
}
