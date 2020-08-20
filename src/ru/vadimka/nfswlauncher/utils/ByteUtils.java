package ru.vadimka.nfswlauncher.utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;

import ru.vadimka.nfswlauncher.Log;

public class ByteUtils {
	/**
	 * Преобразовать строку HEX в массив байтов
	 * В строке должны быть HEX байты, без запятых и пробелов
	 * @param str Строка, в которой HEX символы
	 * @return byte[]
	 */
	public static byte[] hexStringToBytes(String str) {
		int len = str.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			try {
				data[i / 2] = (byte) ((Character.digit(str.charAt(i), 16) << 4)
						+ Character.digit(str.charAt(i+1), 16));
			} catch (StringIndexOutOfBoundsException e) {
				Log.getLogger().log(Level.WARNING,"Ошибка, при попытке переконвертировать hex строку в байты",e);
			}
		}
		return data;
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
			if (!raw) {
				if (i != 0) data += ", ";
				if (i != 0 && i % 4 == 0) data += "\n";
				data += "(byte) 0x";
			}
			data += Integer.toHexString((bytes[i] >> 4) & 0xf)+""+Integer.toHexString(bytes[i] & 0xf);
		}
		return data;
	}
	/**
	 * Получить контрольную сумму файла
	 * @param path - Путь к файлу
	 * @return контрольная сума или byte[0] в случае ошибки
	 */
	public static byte[] getCheckSum(InputStream fis) {
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
}
