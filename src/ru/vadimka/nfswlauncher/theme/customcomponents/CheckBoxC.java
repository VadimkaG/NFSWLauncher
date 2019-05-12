package ru.vadimka.nfswlauncher.theme.customcomponents;

import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

public class CheckBoxC extends JCheckBox {

	private static final long serialVersionUID = -1952640317275786431L;
	
	public CheckBoxC(ActionListener act) {
		super();
		addActionListener(act);
	}
	public CheckBoxC(boolean b, ActionListener act) {
		super();
		super.setSelected(b);
		addActionListener(act);
	}

}
