package ru.vadimka.nfswlauncher.theme.customcomponents;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

import ru.vadimka.nfswlauncher.theme.manager.StyleItem;

public class FieldC extends JTextField implements FocusListener {

	private static final long serialVersionUID = -8913634885412342253L;
	
	private String alias = "";
	
	private String value = "";
	private String HINT = "";
	private boolean showingHint = false;
	
	public FieldC() {
		super();
	}
	
	public FieldC(int i,String str) {
		super(i);
		alias = str;
	}
	public FieldC(int i,String str, String hint) {
		super(i);
		alias = str;
		HINT = hint;
		super.setText(HINT);
		showingHint = true;
		addFocusListener(this);
	}
	public void setStyle(StyleItem style) {
		if (style.getBackground() != null)
			setBackground(style.getBackground());
		if (style.getColorText() != null)
			setForeground(style.getColorText());
	}
	/*public FieldC genGetter(Linkable obj) {
		FieldC c = this;
		obj.link(alias, new Getter<String>() {
			@Override
			public String get() {
				return c.getText();
			}
		});
		return this;
	}*/
	@Override
	public void focusGained(FocusEvent arg0) {
		if(this.getText().isEmpty()) {
			super.setText(value);
			showingHint = false;
		}
	}
	@Override
	public void focusLost(FocusEvent arg0) {
		if(this.getText().isEmpty()) {
			super.setText(HINT);
			showingHint = true;
		}
	}
	@Override
	public String getText() {
		return showingHint ? "" : super.getText();
	}
}
