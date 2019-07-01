package ru.vadimka.nfswlauncher.theme.customcomponents;

import javax.swing.JPasswordField;

import ru.vadimka.nfswlauncher.theme.manager.StyleItem;

public class PassFieldC extends JPasswordField implements Stylisable {

	private static final long serialVersionUID = -5881740635140730506L;
	
	private String alias = "";
	
	public PassFieldC() {
		super();
	}

	public PassFieldC(int i, String str) {
		super(i);
		alias = str;
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
}
