package ru.vadimka.nfswlauncher.theme.customcomponents;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.swing.JPasswordField;

import ru.vadimka.nfswlauncher.Log;
import ru.vadimka.nfswlauncher.theme.manager.StyleItem;

public class PassFieldC extends JPasswordField implements Stylisable {

	private static final long serialVersionUID = -5881740635140730506L;
	
	//private String alias = "";
	
	public PassFieldC() {
		super();
	}

	public PassFieldC(int i, String str) {
		super(i);
		//alias = str;
	}
	public void setStyle(StyleItem style) {
		if (style.getBackground() != null)
			setBackground(style.getBackground());
		if (style.getColorText() != null)
			setForeground(style.getColorText());
	}
	/*public PassFieldC genGetter(Linkable obj) {
		PassFieldC c = this;
		obj.link(alias, new Getter<String>() {
			@SuppressWarnings("deprecation")
			@Override
			public String get() {
				return DigestUtils.sha1Hex(c.getText());
			}
		});
		return this;
	}*/
	/**
	 * Получить зашифрованный пароль
	 */
	public String getPasswordSha1() {
		char[] in = getPassword();
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			Log.getLogger().warning("Не найден алгоритм шифрования пароля.");
			return "";
		}
		ArrayList<Byte> list = new ArrayList<Byte>();
		for(int i = 0; i<in.length; i++){
			byte b = (byte) in[i];
			list.add(b);
		}
		byte[] inputInByte = new byte[list.size()];
		for(int i =0;i<list.size();i++){
			inputInByte[i] = list.get(i);
		}
		md.update(inputInByte);
		byte byteData[] = md.digest();
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			String hex = Integer.toHexString(0xff & byteData[i]);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}
}
