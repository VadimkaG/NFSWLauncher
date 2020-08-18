package ru.vadimka.nfswlauncher.theme;

import java.util.List;

import ru.vadimka.nfswlauncher.Locale;
import ru.vadimka.nfswlauncher.ValueObjects.ServerVO;

public class NullGUI implements GraphModule {
	
	public NullGUI() {
		
	}

	@Override
	public void loading() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadingComplite() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLogin(boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void errorDialog(String text, String title) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void infoDialog(String text, String title) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean questionDialog(String text, String title) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setVisible(boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateServers(List<ServerVO> servers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateLocales(Locale[] locales) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDownloadState(boolean Status) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDownloadState(boolean Status, int MaxValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void DownloadStateAddValue() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String fileSelect(String path, boolean itsFile) {
		// TODO Auto-generated method stub
		return null;
	}

}
