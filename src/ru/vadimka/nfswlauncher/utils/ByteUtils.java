package ru.vadimka.nfswlauncher.utils;

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
		data[i / 2] = (byte) ((Character.digit(str.charAt(i), 16) << 4)
				+ Character.digit(str.charAt(i+1), 16));
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
			if (i != 0 && !raw) data += ", ";
			if (!raw) data += "(byte) 0x";
			data += Integer.toHexString((bytes[i] >> 4) & 0xf)+""+Integer.toHexString(bytes[i] & 0xf);
		}
		return data;
	}
}
