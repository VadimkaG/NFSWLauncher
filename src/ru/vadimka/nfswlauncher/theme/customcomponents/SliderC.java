package ru.vadimka.nfswlauncher.theme.customcomponents;

import javax.swing.JSlider;

import ru.vadimka.nfswlauncher.theme.manager.StyleItem;

public class SliderC extends JSlider implements Stylisable {

	private static final long serialVersionUID = 5728333020970934421L;

	private String alias = "";
	
	public SliderC(int i, String str) {
		super();
		super.setValue(i);
		alias = str;
	}

	@Override
	public void setStyle(StyleItem style) {
		// TODO Auto-generated method stub
		
	}
	/*public SliderC genGetter(Linkable obj) {
		SliderC c = this;
		obj.link(alias, new Getter<String>() {
			@Override
			public String get() {
				return String.valueOf(c.getValue());
			}
		});
		return this;
	}*/

}
