package ru.vadimka.nfswlauncher.theme.customcomponents;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.plaf.basic.BasicComboBoxUI;

public class PaintComboBox<E> extends JComboBox<E> {
	
	private static final long serialVersionUID = 2419402066729564102L;
	
	public PaintComboBox() {
		setOpaque(false);
		
		setUI(new BasicComboBoxUI() {
			@Override
			protected JButton createArrowButton() {
				return new JButton() {
					private static final long serialVersionUID = -5271948868503896425L;
					@Override
					public int getWidth() {
						return 0;
					}
				};
			}
		});
	}
	
}
