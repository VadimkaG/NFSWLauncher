package ru.vadimka.nfswlauncher.theme.customcomponents;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import ru.vadimka.nfswlauncher.theme.manager.StyleItem;

public class LabelC extends JLabel implements Stylisable {

	private static final long serialVersionUID = -1373213441286593659L;

	public LabelC(String text) {
		super(text);
	}
	public LabelC(String text, StyleItem style) {
		super(text);
		setStyle(style);
	}
	public LabelC() {
		super();
	}
	public void setStyle(StyleItem style) {
		if (style.getBackground() != null)
			setBackground(style.getBackground());
		if (style.getColorText() != null)
			setForeground(style.getColorText());
		if (style.textCentred())
			setHorizontalAlignment(SwingConstants.CENTER);
	}
}
