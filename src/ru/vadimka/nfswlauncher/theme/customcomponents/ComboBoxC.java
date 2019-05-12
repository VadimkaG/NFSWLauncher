package ru.vadimka.nfswlauncher.theme.customcomponents;

import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import ru.vadimka.nfswlauncher.actions.Getter;
import ru.vadimka.nfswlauncher.actions.Linkable;
import ru.vadimka.nfswlauncher.theme.manager.StyleItem;

public class ComboBoxC<T> extends JComboBox<T> {

	private static final long serialVersionUID = 6069568809110455625L;
	
	private String alias = "";
	
	public ComboBoxC(ActionListener act,String str) {
		super();
		alias = str;
		addActionListener(act);
	}
	
	public void setStyle(StyleItem style) {
		// Не реализовано
	}
	public ComboBoxC<T> genGetter(Linkable obj) {
		ComboBoxC<T> c = this;
		obj.link(alias, new Getter<String>() {
			@Override
			public String get() {
				return c.getSelectedItem().toString();
			}
		});
		return this;
	}

}
